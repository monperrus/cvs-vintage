/*
 * Copyright (c) 1997-1999 The Java Apache Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache 
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * 4. The names "Apache JServ", "Apache JServ Servlet Engine" and 
 *    "Java Apache Project" must not be used to endorse or promote products 
 *    derived from this software without prior written permission.
 *
 * 5. Products derived from this software may not be called "Apache JServ"
 *    nor may "Apache" nor "Apache JServ" appear in their names without 
 *    prior written permission of the Java Apache Project.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache 
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *    
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Group. For more information
 * on the Java Apache Project and the Apache JServ Servlet Engine project,
 * please see <http://java.apache.org/>.
 *
 */

/***************************************************************************
 * Description: ISAPI plugin for IIS/PWS                                   *
 * Author:      Gal Shachor <shachor@il.ibm.com>                           *
 * Version:     $Revision: 1.1 $                                               *
 ***************************************************************************/

#include <httpext.h>
#include <httpfilt.h>
#include <wininet.h>

#include "jk_global.h"
#include "jk_util.h"
#include "jk_map.h"
#include "jk_pool.h"
#include "jk_service.h"
#include "jk_worker.h"
#include "jk_ajp12_worker.h"
#include "jk_uri_worker_map.h"

#define VERSION_STRING "Jakarta/ISAPI/1.0b1"

#define REGISTRY_LOCATION       ("Software\\Apache Software Foundation\\Jakarta Isapi Redirector\\1.0")
#define EXTENSION_URI_TAG       ("extension_uri")

#define GET_SERVER_VARIABLE_VALUE(name, place) {    \
    huge_buf_sz = sizeof(huge_buf);                 \
    if(get_server_value(private_data->lpEcb,        \
                        (name),                     \
                        huge_buf,                   \
                        huge_buf_sz,                \
                        "")) {                      \
        (place) = jk_pool_strdup(&private_data->p, huge_buf);   \
    }   \
}\

#define GET_SERVER_VARIABLE_VALUE_INT(name, place, def) {   \
    huge_buf_sz = sizeof(huge_buf);                 \
    if(get_server_value(private_data->lpEcb,        \
                        (name),                     \
                        huge_buf,                   \
                        huge_buf_sz,                \
                        "")) {                      \
        (place) = atoi(huge_buf);                   \
        if(0 == (place)) {                          \
            (place) = def;                          \
        }                                           \
    } else {    \
        (place) = def;  \
    }           \
}\
/*
 * We use a TLS to pass values from the filter to the 
 * extension. These values are:
 *
 * 1. The real URI before redirection took place
 * 2. The name of the worker to be used.
 *
 */

static DWORD tls_index = 0xFFFFFFFF;
static int   is_inited = JK_FALSE;
static jk_uri_worker_map_t *uw_map = NULL; 
static jk_logger_t *logger = NULL; 

static char extension_uri[INTERNET_MAX_URL_LENGTH] = "/jakarta/isapi_redirect.dll";
static char log_file[MAX_PATH * 2];
static int  log_level = JK_LOG_EMERG_LEVEL;
static char worker_file[MAX_PATH * 2];
static char worker_mount_file[MAX_PATH * 2];

struct filter_ext_record {
    char *worker;
    char *uri;
};
typedef struct filter_ext_record filter_ext_record_t;

struct isapi_private_data {
    jk_pool_t p;
    
    int request_started;
    unsigned bytes_read_so_far;
    LPEXTENSION_CONTROL_BLOCK  lpEcb;
};
typedef struct isapi_private_data isapi_private_data_t;


static int JK_METHOD start_response(jk_ws_service_t *s,
                                    int status,
                                    char *reason,
                                    const char * const *header_names,
                                    const char * const *header_values,
                                    unsigned num_of_headers);

static int JK_METHOD read(jk_ws_service_t *s,
                          void *b,
                          unsigned l,
                          unsigned *a);

static int JK_METHOD write(jk_ws_service_t *s,
                           const void *b,
                           unsigned l);

static int init_ws_service(isapi_private_data_t *private_data,
                           jk_ws_service_t *s,
                           char **worker_name);

static int initialize_extension(void);

static int read_registry_init_data(void);

static int get_registry_config_parameter(HKEY hkey,
                                         const char *tag, 
                                         char *b,
                                         DWORD sz);


static int get_server_value(LPEXTENSION_CONTROL_BLOCK lpEcb,
                            char *name,
                            char  *buf,
                            DWORD bufsz,
                            char  *def_val);

static int JK_METHOD start_response(jk_ws_service_t *s,
                                    int status,
                                    char *reason,
                                    const char * const *header_names,
                                    const char * const *header_values,
                                    unsigned num_of_headers)
{
    static char crlf[3] = { (char)13, (char)10, '\0' };

    if(status < 100 || status > 1000) {
        jk_log(logger, JK_LOG_ERROR, "jk_ws_service_t::start_response, invalid status %d\n", status);
        return JK_FALSE;
    }

    if(s && s->ws_private) {
        isapi_private_data_t *p = s->ws_private;
        if(!p->request_started) {
            DWORD len_of_status;
            char *status_str;
            char *headers_str;

            p->request_started = JK_TRUE;

            /*
             * Create the status line
             */
            if(!reason) {
                reason = "";
            }
            status_str = (char *)_alloca((6 + strlen(reason)) * sizeof(char));
            sprintf(status_str, "%d %s", status, reason);
            len_of_status = strlen(status_str); 
        
            /*
             * Create response headers string
             */
            if(num_of_headers) {
                unsigned i;
                unsigned len_of_headers;
                for(i = 0 , len_of_headers = 0 ; i < num_of_headers ; i++) {
                    len_of_headers += strlen(header_names[i]);
                    len_of_headers += strlen(header_values[i]);
                    len_of_headers += 3; /* extra for : and crlf */
                }

                len_of_headers += 3;  /* crlf and terminating null char */
                headers_str = (char *)_alloca(len_of_headers * sizeof(char));
                headers_str[0] = '\0';

                for(i = 0 ; i < num_of_headers ; i++) {
                    strcat(headers_str, header_names[i]);
                    strcat(headers_str, ":");
                    strcat(headers_str, header_values[i]);
                    strcat(headers_str, crlf);
                }
                strcat(headers_str, crlf);
            } else {
                headers_str = crlf;
            }

            if(!p->lpEcb->ServerSupportFunction(p->lpEcb->ConnID, 
                                                HSE_REQ_SEND_RESPONSE_HEADER,
                                                status_str,
                                                (LPDWORD)&len_of_status,
                                                (LPDWORD)headers_str)) {
                jk_log(logger, JK_LOG_ERROR, 
                       "jk_ws_service_t::start_response, ServerSupportFunction failed\n");
                return JK_FALSE;
            }       


        }
        return JK_TRUE;

    }

    jk_log(logger, JK_LOG_ERROR, 
           "jk_ws_service_t::start_response, NULL parameters\n");
    return JK_FALSE;
}

static int JK_METHOD read(jk_ws_service_t *s,
                          void *b,
                          unsigned l,
                          unsigned *a)
{
    if(s && s->ws_private && b && a) {
        isapi_private_data_t *p = s->ws_private;
        
        *a = 0;
        if(l) {
            char *buf = b;
            DWORD already_read = p->lpEcb->cbAvailable - p->bytes_read_so_far;
            
            if(already_read >= l) {
                memcpy(buf, p->lpEcb->lpbData + p->bytes_read_so_far, l);
                p->bytes_read_so_far += l;
                *a = l;
            } else {
                /*
                 * Try to copy what we already have 
                 */
                if(already_read > 0) {
                    memcpy(buf, p->lpEcb->lpbData + p->bytes_read_so_far, already_read);
                    buf += already_read;
                    l   -= already_read;
                    p->bytes_read_so_far = p->lpEcb->cbAvailable;
                    
                    *a = already_read;
                }
                
                /*
                 * Now try to read from the client ...
                 */
                if(p->lpEcb->ReadClient(p->lpEcb->ConnID, buf, &l)) {
                    *a += l;            
                } else {
                    jk_log(logger, JK_LOG_ERROR, "jk_ws_service_t::read, ReadClient failed\n");
                    return JK_FALSE;
                }                   
            }
        }
        return JK_TRUE;
    }

    jk_log(logger, JK_LOG_ERROR, "jk_ws_service_t::read, NULL parameters\n");
    return JK_FALSE;
}

static int JK_METHOD write(jk_ws_service_t *s,
                           const void *b,
                           unsigned l)
{
    if(s && s->ws_private && b) {
        isapi_private_data_t *p = s->ws_private;

        if(l) {
            unsigned written = 0;           
            char *buf = (char *)b;

            if(!p->request_started) {
                start_response(s, 200, NULL, NULL, NULL, 0);
            }

            while(written < l) {
                DWORD try_to_write = l - written;
                if(!p->lpEcb->WriteClient(p->lpEcb->ConnID, 
                                          buf + written, 
                                          &try_to_write, 
                                          0)) {
                    jk_log(logger, JK_LOG_ERROR, "jk_ws_service_t::write, WriteClient failed\n");
                    return JK_FALSE;
                }
                written += try_to_write;
            }
        }

        return JK_TRUE;

    }
    jk_log(logger, JK_LOG_ERROR, "jk_ws_service_t::write, NULL parameters\n");
    return JK_FALSE;
}

BOOL WINAPI GetFilterVersion(PHTTP_FILTER_VERSION pVer)
{
    ULONG http_filter_revision = HTTP_FILTER_REVISION 

    pVer->dwFilterVersion = pVer->dwServerFilterVersion;
                        
    if(pVer->dwFilterVersion > http_filter_revision) {
        pVer->dwFilterVersion = http_filter_revision;
    }

    pVer->dwFlags = SF_NOTIFY_ORDER_HIGH        | 
                    SF_NOTIFY_SECURE_PORT       | 
                    SF_NOTIFY_NONSECURE_PORT    |
                    SF_NOTIFY_PREPROC_HEADERS;
                    
    strcpy(pVer->lpszFilterDesc, VERSION_STRING);

    if(!is_inited) {
        return initialize_extension();
    }

    return TRUE;
}

DWORD WINAPI HttpFilterProc(PHTTP_FILTER_CONTEXT pfc,
                            DWORD dwNotificationType, 
                            LPVOID pvNotification)
{
    if(is_inited &&
       (SF_NOTIFY_PREPROC_HEADERS == dwNotificationType)) { 
        PHTTP_FILTER_PREPROC_HEADERS p = (PHTTP_FILTER_PREPROC_HEADERS)pvNotification;
        char uri[INTERNET_MAX_URL_LENGTH]; 
        char *query;
        DWORD sz = sizeof(uri);

        if(p->GetHeader(pfc, "url", (LPVOID)uri, (LPDWORD)&sz)) {
            if(strlen(uri)) {
                char *worker;
                query = strchr(uri, '?');
                if(query) {
                    *query = '\0';
                }
                jk_log(logger, JK_LOG_DEBUG, "In HttpFilterProc test redirection of %s\n", uri);
                worker = map_uri_to_worker(uw_map, uri, logger);                
                if(query) {
                    *query = '?';
                }

                if(worker) {
                    /* This is a servlet, should redirect ... */
                    filter_ext_record_t *rec;

                    jk_log(logger, JK_LOG_DEBUG, 
                           "In HttpFilterProc %s should redirect to %s\n", 
                           uri, worker);

                    rec = pfc->AllocMem(pfc, sizeof(filter_ext_record_t), 0);
                    if(rec) {
                        rec->uri = pfc->AllocMem(pfc, strlen(uri) + 1, 0);
                        rec->worker = pfc->AllocMem(pfc, strlen(worker) + 1, 0);
                        if(rec->uri && rec->worker && TlsSetValue(tls_index, rec)) {
                            strcpy(rec->uri, uri);
                            strcpy(rec->worker, worker);
                            p->SetHeader(pfc, "url", extension_uri);
                        }
                    }
                } else {
                    /* check if asking for web-inf file */
                    char *c = uri;                    

                    while(*c) {
                        *c = tolower(*c);
                        c++;
                    }                    
                    if(strstr(uri, "web-inf")) {
                        char crlf[3] = { (char)13, (char)10, '\0' };
                        char ctype[30];
                        char *msg = "<HTML><BODY><H1>Access is Forbidden</H1></BODY></HTML>";
                        DWORD len = strlen(msg);

                        sprintf(ctype, 
                                "Content-Type:text/html%s%s", 
                                crlf, 
                                crlf);

                        /* reject !!! */
                        pfc->ServerSupportFunction(pfc, 
                                                   SF_REQ_SEND_RESPONSE_HEADER,
                                                   "403 Forbidden",
                                                   (DWORD)crlf,
                                                   (DWORD)ctype);
                        pfc->WriteClient(pfc, msg, &len, 0);

                        return SF_STATUS_REQ_FINISHED;
                    }
                }
            }
        }
    }
    return SF_STATUS_REQ_NEXT_NOTIFICATION;
}


BOOL WINAPI GetExtensionVersion(HSE_VERSION_INFO  *pVer)
{
    pVer->dwExtensionVersion = MAKELONG( HSE_VERSION_MINOR,
                                         HSE_VERSION_MAJOR );

    strcpy(pVer->lpszExtensionDesc, VERSION_STRING);


    if(!is_inited) {
        return initialize_extension();
    }

    return TRUE;
}

DWORD WINAPI HttpExtensionProc(LPEXTENSION_CONTROL_BLOCK  lpEcb)
{   
    DWORD rc = HSE_STATUS_ERROR;

    lpEcb->dwHttpStatusCode = HTTP_STATUS_SERVER_ERROR;

    if(is_inited) {
        isapi_private_data_t private_data;
        jk_ws_service_t s;
        jk_pool_atom_t buf[SMALL_POOL_SIZE];
        char *worker_name;

        jk_open_pool(&private_data.p, buf, sizeof(buf));

        private_data.request_started = JK_FALSE;
        private_data.bytes_read_so_far = 0;
        private_data.lpEcb = lpEcb;

        s.ws_private = &private_data;

        if(init_ws_service(&private_data, &s, &worker_name)) {
            jk_worker_t *worker = wc_get_worker_for_name(worker_name, logger);
            if(worker) {
                jk_endpoint_t *e = NULL;
                if(worker->get_endpoint(worker, &e, logger)) {
                    if(e->service(e, &s, logger)) {
                        rc = HSE_STATUS_SUCCESS_AND_KEEP_CONN;
                        lpEcb->dwHttpStatusCode = HTTP_STATUS_OK;
                    }
                    e->done(&e, logger);
                }
            }
        }
        jk_close_pool(&private_data.p);     
    }

    return rc;
}

    

BOOL WINAPI TerminateExtension(DWORD dwFlags) 
{
    return TerminateFilter(dwFlags);
}

BOOL WINAPI TerminateFilter(DWORD dwFlags) 
{
    if(is_inited) {
        is_inited = JK_FALSE;

        uri_worker_map_free(&uw_map, logger);
        wc_close(logger);
        if(logger) {
            jk_close_file_logger(&logger);
        }
    }
    
    return TRUE;
}


BOOL WINAPI DllMain(HINSTANCE hInst,        // Instance Handle of the DLL
                    ULONG ulReason,         // Reason why NT called this DLL
                    LPVOID lpReserved)      // Reserved parameter for future use
{
    BOOL fReturn = TRUE;

    switch (ulReason) {
        case DLL_PROCESS_ATTACH:
            tls_index = TlsAlloc();
            if(0xFFFFFFFF == tls_index) {
                fReturn =  FALSE;
            }
            TlsSetValue(tls_index, NULL);
        break;

        case DLL_PROCESS_DETACH:
            __try {
                TerminateFilter(HSE_TERM_MUST_UNLOAD);
            } __except(1) {
            }

            if(0xFFFFFFFF != tls_index) {
                TlsFree(tls_index);
                tls_index = 0xFFFFFFFF;
            }
        break;

        default:
        break;
    } 

    return fReturn;
}

static int initialize_extension(void)
{
    int rc = JK_FALSE;  

    if(read_registry_init_data()) {
        jk_map_t *map;

        if(!jk_open_file_logger(&logger, log_file, log_level)) {
            logger = NULL;
        }
        
        if(map_alloc(&map)) {
            if(map_read_properties(map, worker_mount_file)) {
                if(uri_worker_map_alloc(&uw_map, map, logger)) {
                    rc = JK_TRUE;
                }
            }
            map_free(&map);
        }

        if(rc) {
            rc = JK_FALSE;
            if(map_alloc(&map)) {
                if(map_read_properties(map, worker_file)) {
                    if(wc_open(map, logger)) {
                        rc = JK_TRUE;
                    }
                }
                map_free(&map);
            }
        }

    }

    if(rc) {
        is_inited = JK_TRUE;
    }
    return rc;
}

static int read_registry_init_data(void)
{
    char tmpbuf[INTERNET_MAX_URL_LENGTH];
    HKEY hkey;
    long rc;
    int  ok = JK_TRUE;
    rc = RegOpenKeyEx(HKEY_LOCAL_MACHINE,
                      REGISTRY_LOCATION,
                      (DWORD)0,         
                      KEY_READ,         
                      &hkey);            
    if(ERROR_SUCCESS != rc) {
        return JK_FALSE;
    } 

    if(get_registry_config_parameter(hkey,
                                     JK_LOG_FILE_TAG, 
                                     tmpbuf,
                                     sizeof(log_file))) {
        strcpy(log_file, tmpbuf);
    } else {
        ok = JK_FALSE;
    }
    
    if(get_registry_config_parameter(hkey,
                                     JK_LOG_LEVEL_TAG, 
                                     tmpbuf,
                                     sizeof(tmpbuf))) {
        log_level = jk_parse_log_level(tmpbuf);
    } else {
        ok = JK_FALSE;
    }

    if(get_registry_config_parameter(hkey,
                                     EXTENSION_URI_TAG, 
                                     tmpbuf,
                                     sizeof(extension_uri))) {
        strcpy(extension_uri, tmpbuf);
    } else {
        ok = JK_FALSE;
    }

    if(get_registry_config_parameter(hkey,
                                     JK_WORKER_FILE_TAG, 
                                     tmpbuf,
                                     sizeof(worker_file))) {
        strcpy(worker_file, tmpbuf);
    } else {
        ok = JK_FALSE;
    }

    if(get_registry_config_parameter(hkey,
                                     JK_MOUNT_FILE_TAG, 
                                     tmpbuf,
                                     sizeof(worker_mount_file))) {
        strcpy(worker_mount_file, tmpbuf);
    } else {
        ok = JK_FALSE;
    }

    RegCloseKey(hkey);

    return ok;
}

static int get_registry_config_parameter(HKEY hkey,
                                         const char *tag, 
                                         char *b,
                                         DWORD sz)
{   
    DWORD type = 0;
    LONG  lrc;

    lrc = RegQueryValueEx(hkey,     
                          tag,      
                          (LPDWORD)0,
                          &type,    
                          (LPBYTE)b,
                          &sz); 
    if((ERROR_SUCCESS != lrc) || (type != REG_SZ)) {
        return JK_FALSE;        
    }
    
    b[sz] = '\0';

    return JK_TRUE;     
}

static int init_ws_service(isapi_private_data_t *private_data,
                           jk_ws_service_t *s,
                           char **worker_name) 
{
    char huge_buf[16 * 1024]; /* should be enough for all */
    DWORD huge_buf_sz;
    filter_ext_record_t *from_filt = (filter_ext_record_t *)TlsGetValue(tls_index);

    s->start_response = start_response;
    s->read = read;
    s->write = write;

    GET_SERVER_VARIABLE_VALUE("AUTH_TYPE", s->auth_type);
    GET_SERVER_VARIABLE_VALUE("REMOTE_USER", s->remote_user);
    GET_SERVER_VARIABLE_VALUE("SERVER_PROTOCOL", s->protocol);
    GET_SERVER_VARIABLE_VALUE("REMOTE_HOST", s->remote_host);
    GET_SERVER_VARIABLE_VALUE("REMOTE_ADDR", s->remote_ip);
    GET_SERVER_VARIABLE_VALUE("SERVER_NAME", s->server_name);
    GET_SERVER_VARIABLE_VALUE_INT("SERVER_PORT", s->server_port, 80)
    GET_SERVER_VARIABLE_VALUE("SERVER_SOFTWARE", s->server_software);
    GET_SERVER_VARIABLE_VALUE_INT("SERVER_PORT_SECURE", s->is_ssl, 0);

    s->method           = private_data->lpEcb->lpszMethod;
    s->content_length   = private_data->lpEcb->cbTotalBytes;
    s->content_type     = private_data->lpEcb->lpszContentType;

    if(from_filt) {
        char *t = strchr(from_filt->uri, '?');
        if(t) {
            *t = '\0';
            t++;
            if(!strlen(t)) {
                t = NULL;
            }
        }
        s->query_string = t;
        s->req_uri      = from_filt->uri;
        *worker_name    = from_filt->worker;
    } else {
        s->query_string = private_data->lpEcb->lpszQueryString;
        *worker_name    = JK_AJP12_WORKER_NAME;
        GET_SERVER_VARIABLE_VALUE("URL", s->req_uri);       
    }
    TlsSetValue(tls_index, NULL);       

    s->ssl_cert     = NULL;
    s->ssl_cert_len = 0;
    s->ssl_cipher   = NULL;

    if(s->is_ssl) {
        s->scheme = "https";
    } else {
        s->scheme = "http";
    }

    s->headers_names    = NULL;
    s->headers_values   = NULL;
    s->num_headers      = 0;

    huge_buf_sz = sizeof(huge_buf);         
    if(get_server_value(private_data->lpEcb,
                        "ALL_HTTP",             
                        huge_buf,           
                        huge_buf_sz,        
                        "")) {              
        unsigned cnt = 0;
        char *tmp;

        for(tmp = huge_buf ; *tmp ; tmp++) {
            if(*tmp == '\n'){
                cnt++;
            }
        }

        if(cnt) {
            char *headers_buf = jk_pool_strdup(&private_data->p, huge_buf);
            unsigned i;
            unsigned len_of_http_prefix = strlen("HTTP_");

            s->headers_names  = jk_pool_alloc(&private_data->p, cnt * sizeof(char *));
            s->headers_values = jk_pool_alloc(&private_data->p, cnt * sizeof(char *));

            if(!s->headers_names || !s->headers_values || !headers_buf) {
                return JK_FALSE;
            }

            for(i = 0, tmp = headers_buf ; *tmp && i < cnt ; i++) {

                /* Skipp the HTTP_ prefix to the beginning of th header name */
                tmp += len_of_http_prefix;
                s->headers_names[i] = tmp;
                while(':' != *tmp && *tmp) {
                    if('_' == *tmp) {
                        *tmp = '-';
                    } else {
                        *tmp = tolower(*tmp);
                    }
                    tmp++;
                }
                *tmp = '\0';
                tmp++;

                /* Skipp all the WS chars after the ':' to the beginning of th header value */
                while(' ' == *tmp || '\t' == *tmp || '\v' == *tmp) {
                    tmp++;
                }

                s->headers_values[i]  = tmp;

                while(*tmp != '\n' && *tmp != '\r') {
                    tmp++;
                }
                *tmp = '\0';
                tmp++;

                /* skipp CR LF */
                while(*tmp == '\n' || *tmp == '\r') {
                    tmp++;
                }

            }
            s->num_headers = cnt;
        }
    }
   
    return JK_TRUE;
}

static int get_server_value(LPEXTENSION_CONTROL_BLOCK lpEcb,
                            char *name,
                            char  *buf,
                            DWORD bufsz,
                            char  *def_val)
{
    if(!lpEcb->GetServerVariable(lpEcb->ConnID, 
                                 name,
                                 buf,
                                 (LPDWORD)&bufsz)) {
        strcpy(buf, def_val);
        return JK_FALSE;
    }

    if(bufsz > 0) {
        buf[bufsz - 1] = '\0';
    }

    return JK_TRUE;
}