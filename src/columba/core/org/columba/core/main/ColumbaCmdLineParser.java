//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.

package org.columba.core.main;

import jargs.gnu.CmdLineParser;

import org.columba.core.config.ConfigPath;

/**
 * Parsing the commandline arguments and setting states, that can be used
 * from other components.
 *
 * @author waffel
 */
public class ColumbaCmdLineParser {
    private static CmdLineParser.Option help;
    private static CmdLineParser.Option version;
    private static CmdLineParser.Option debug;
    private static CmdLineParser.Option composer;
    private static CmdLineParser.Option rcpt;
    private static CmdLineParser.Option body;
    private static CmdLineParser.Option path;
    private static CmdLineParser.Option mailurl;
    private static CmdLineParser.Option subject;
    private static CmdLineParser.Option cc;
    private static CmdLineParser.Option bcc;
    
    protected CmdLineParser parser;
    private boolean debugOption = false;
    private String pathOption = null;
    private String rcptOption = null;
    private String bodyOption = null;
    private boolean composerOption = false;
    private String mailurlOption = null;
    private String subjectOption = null;
    private String ccOption = null;
    private String bccOption = null;

    public ColumbaCmdLineParser() {
        parser = new CmdLineParser();
    }
    
    /**
     * Parsing the commandline arguments and set the given values to the commandline arguments.
     * @param args commandline arguments to be parsed
     */
    public void initCmdLine(String[] args) {
        // setting any options
        // the short option '+' is an hack until jargs supports also "only long commands"
        // TODO make options i18n compatible
        help = parser.addBooleanOption('+', "help");
        version = parser.addBooleanOption('+', "version");
        debug = parser.addBooleanOption('d', "debug");
        composer = parser.addBooleanOption('c', "composer");
        rcpt = parser.addStringOption('r', "rcpt");
        body = parser.addStringOption('b', "body");
        path = parser.addStringOption('p', "path");
        mailurl = parser.addStringOption('+', "mailurl");
        subject = parser.addStringOption('s', "subject");
        cc = parser.addStringOption('+', "cc");
        bcc = parser.addStringOption('+', "bcc");

        try {
            parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            printUsage();
            System.exit(2);
        }

        checkHelp();
        checkVersion();
        checkDebug();
        checkPath();
        checkRCPT();
        checkBody();
        checkComposer();
        checkSubject();
        checkMailurl();
    }

    /**
     * Check the commandLineArgument help. If the argument help is given, then a help text is
     * printed to standard out and the program exists.
     * @param helpOpt help Option @see CmdLineParser.Option
     * @param parser parser which parsed the option
     */
    private void checkHelp() {
        Boolean helpValue = (Boolean) parser.getOptionValue(help);

        if (helpValue != null) {
            if (helpValue.booleanValue()) {
                printUsage();
                System.exit(0);
            }
        }
    }

    /**
     * Check if the commandline argument --version is given. If this is true the version text is
     * printed out to stdandard out and the program exit.
     * @param versionOpt Verion Option @see CmdLineParser.Option
     * @param parser parser which parsed the option
     */
    private void checkVersion() {
        Boolean versionValue = (Boolean) parser.getOptionValue(version);

        if (versionValue != null) {
            if (versionValue.booleanValue()) {
                printVersionInfo();
                System.exit(0);
            }
        }
    }

    /**
     * Checks if the commandline argument -d,--debug is given, if this is true the intern debugValue
     * is set to true @see ColumbaCmdLineParser#setDebugOption(boolean) else the option is set to
     * false. You can get the option via @see ColumbaCmdLineParser#isDebugOption()
     * @param debugOpt Option for debug @see CmdLineParser.Option
     * @param parser parser which parsed the option
     */
    private void checkDebug() {
        Boolean debugValue = (Boolean) parser.getOptionValue(debug);

        if (debugValue != null) {
            setDebugOption(debugValue.booleanValue());
        }
    }

    /**
     * Checks if the commandline option -p,--path is given, if this is true a new ConfigPath with the path
     * to the configs is generated, else a empty (default) ConfigPath Object is created
     * @param pathOpt the path option @see CmdLineParser.Option
     * @param parser parser which parsed the Option
     */
    private void checkPath() {
        String pathValue = (String) parser.getOptionValue(path);
        setPathOption(pathValue);

        if (pathValue != null) {
            new ConfigPath(pathValue);
        } else {
            new ConfigPath();
        }
    }

    /** Checks if the commandline argument -r,--rcpt (recipient) is given. If this is true the intern
     * value for recipient is set @see ColumbaCmdLineParser#setRcptOption(String) ,else the option
     * is set to null. You can access it via @see ColumbaCmdLineParser#getRcptOption()
     * @param rcptOpt the recipient Option @see CmdLineParser.Option
     * @param parser parsed which parsed the Option
     */
    private void checkRCPT() {
        String rcptValue = (String) parser.getOptionValue(rcpt);
        setRcptOption(rcptValue);
    }

    /**
     * Checks if the commandline argument -b,--body is given, if this is true, then the intern value
     * for body is set @see ColumbaCmdLineParser#setBodyOption(String) ,else the option is set to
     * null. You can get the option value via @see ColumbaCmdLineParser#getBodyOption()
     * @param bodyOpt the Option for body @see CmdLineParser.Option
     * @param parser parser which parsed the Option
     */
    private void checkBody() {
        String bodyValue = (String) parser.getOptionValue(body);
        setBodyOption(bodyValue);
    }

    /**
     * Checks the option --composer, if this is true the intern composerValue is set
     * @see ColumbaCmdLineParser#setComposerOption(boolean) ,else the option is set to null.
     * You can access this option via @see ColumbaCmdLineParser#getComposerOption()
     * @param composerOpt Composer Option @see CmdLineParser.Option
     * @param parser parser which parsed the Option
     */
    private void checkComposer() {
        Boolean composerValue = (Boolean) parser.getOptionValue(composer);

        if (composerValue != null) {
            setComposerOption(composerValue.booleanValue());
        }
    }

    /**
      * Checks the option --mailurl, if this is true the intern mailurlValue is set
      * @see ColumbaCmdLineParser#setMailurlOption(String), else the option is set to null.
      * You can access this option via @see ColumbaCmdLineParser#getMailurlOption()
      * @param mailurlOpt Composer Option @see CmdLineParser.Option
      * @param parser parser which parsed the Option
      */
    private void checkMailurl() {
        String mailurlValue = (String) parser.getOptionValue(mailurl);

        if (mailurlValue != null) {
            setMailurlOption(mailurlValue);
        }
    }

    /**
     * Checks the option --subject, if this is true the intern subjectValue is set
     * @see ColumbaCmdLineParser#setSubjectOption(String), else the option is set to null.
     * You can access this option via @see ColumbaCmdLineParser#getSubjectOption()
     * @param subjectOpt Composer Option @see CmdLineParser.Option
     * @param parser parser which parsed the Option
     */
    private void checkSubject() {
        String subjectValue = (String) parser.getOptionValue(subject);
        setSubjectOption(subjectValue);
    }

    /**
     * prints the usage of the program with commandline arguments.
     * TODO: all options should be printed
     */
    public static void printUsage() {
        System.out.println("usage: java -jar columba.jar [OPTION]");
        System.out.println();
        System.out.println(
            "Mandatory arguments to long options are mandatory for short options too.");
        System.out.println(
            "  -d, --debug\t\tprints debug informations to standard out");
        System.out.println(
            "  -p, --path\t\tsets the path to the config directory");
        System.out.println(
            "  -c, --composer\tdisplays the composer window on startup");
        System.out.println(
            "  -r, --rcpt\t\tsets the recipient for the composer " +
            "(if the composer argument given)");
        System.out.println(
            "  -b, --body\t\tsets the message body for the composer " +
            "(if the composer argument given)");
        System.out.println(
            "  --mailurl\t\topens the composer window with the specified mailurl info");
        System.out.println("  --subject\t\tsets the subject for the composer " +
            "(if the composer argument given)");
        System.out.println(
            "  --cc\t\t\tsets the Cc for the composer (if the composer argument given)");
        System.out.println("  --bcc\t\t\tsets the Bcc for the composer " +
            "(if the composer argument given)");
        System.out.println("\t--help\t\tdisplay this help and exit");
        System.out.println("\t--version\toutput version information and exit");
        System.out.println();
    }

    /**
     * Prints the current version of columba
     */
    public static void printVersionInfo() {
        System.out.println("columba " + MainInterface.version);
    }

    /**
     * Gives the value of the Body Option.
     * @return the value of the Body Option.
     */
    public String getBodyOption() {
        return bodyOption;
    }

    /**
     * Gives the value of the composer Option.
     * @return the value of the composer Option.
     */
    public boolean getComposerOption() {
        return composerOption;
    }

    /**
     * Gives the value of the Debug option.
     * @return the value of the Debug option.
     */
    public boolean isDebugOption() {
        return debugOption;
    }

    /**
     * Gives the value of the Path Option.
     * @return the value of the Path Option.
     */
    public String getPathOption() {
        return pathOption;
    }

    /**
     * Gives the value of the recipient Option.
     * @return the value of the recipient Option.
     */
    public String getRcptOption() {
        return rcptOption;
    }

    /**
     * Sets the value for the Body Option.
     * @param string the value for the Body Option.
     */
    public void setBodyOption(String string) {
        bodyOption = string;
    }

    /**
     * Sets the value for the composer Option.
     * @param string the value for the composer Option.
     */
    public void setComposerOption(boolean b) {
        composerOption = b;
    }

    /**
     * Sets the value for the Debug Option.
     * @param b the value for the Debug Option.
     */
    public void setDebugOption(boolean b) {
        debugOption = b;
    }

    /**
     * Sets the value for the Path Option.
     * @param string the value for the Path Option.
     */
    public void setPathOption(String string) {
        pathOption = string;
    }

    /**
     * Sets the value for the recipient Option.
     * @param string the value for the recipient Option.
     */
    public void setRcptOption(String string) {
        rcptOption = string;
    }

    /**
     * Gives the value for the Body Option.
     * @return the value for the Body Option.
     */
    public String getBccOption() {
        return bccOption;
    }

    /**
     * Gives the value for the Cc Option.
     * @return the value for the Cc Option.
     */
    public String getCcOption() {
        return ccOption;
    }

    /**
     * Gives the value for the MailUrl Option.
     * @return the value for the MailUrl Option.
     */
    public String getMailurlOption() {
        return mailurlOption;
    }

    /**
     * Gives the value for the subject Option.
     * @return the value for the subject Option.
     */
    public String getSubjectOption() {
        return subjectOption;
    }

    /**
     * Sets the value for the Bcc Option.
     * @param string the value for the Bcc Option.
     */
    public void setBccOption(String string) {
        bccOption = string;
    }

    /**
     * Sets the value for the Cc Option.
     * @param string the value for the Cc Option.
     */
    public void setCcOption(String string) {
        ccOption = string;
    }

    /**
     * Sets the value for the Mailurl Option.
     * @param string the value for the Mailurl Option.
     */
    public void setMailurlOption(String string) {
        mailurlOption = string;
    }

    /**
     * Sets the value for the Subject Option.
     * @param string the value for the Subject Option.
     */
    public void setSubjectOption(String string) {
        subjectOption = string;
    }
}
