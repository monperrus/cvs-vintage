// $Id: GeneratorDummy.java,v 1.9 2004/08/27 05:44:22 linus Exp $
// Copyright (c) 2001-2004 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.language.dummylanguage.generator;

import org.argouml.application.api.Notation;
import org.argouml.application.api.PluggableNotation;
import org.argouml.model.ModelFacade;
import org.argouml.uml.generator.Generator2;

/** 
 * Generator2 subclass to generate text for display in diagrams in in
 * text fields in the ArgoUML user interface.
 * The generated code is a dummy language. The purpose is to provide
 * an illustration of how the Generator and Notation mechanisms are 
 * supposed to work.
 *
 * @author Linus Tolke
 * @stereotype singleton
 */
public final class GeneratorDummy 
	extends Generator2 
	implements PluggableNotation {
    /**
     * The singleton.
     */
    private static final GeneratorDummy SINGLETON = new GeneratorDummy();

    /**
     * Get the one and only instance of this generator.
     *
     * @return The singleton.
     */
    public static GeneratorDummy getInstance() { return SINGLETON; }

    /**
     * Constructor.
     */
    private GeneratorDummy() {
	super(Notation.makeNotation("Dummy", "1.0", null));
    }

    /**
     * <p>Generate code for an extension point.</p>
     *
     * <p>Provided to comply with the interface, but returns null
     *   since no code will be generated. This should prevent a source tab
     *   being shown.</p>
     *
     * @param handle The extension point to generate for
     *
     * @return    The generated code string. Always null in this
     *            implementation.
     */
    public String generateExtensionPoint(Object handle) {
        return "ExtensionPoint(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateOperation(
     *          java.lang.Object, boolean)
     */
    public String generateOperation(Object handle, boolean documented) {
	return "Operation(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateAttribute(
     *          java.lang.Object, boolean)
     */
    public String generateAttribute(Object handle, boolean documented) {
	return "Attribute(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateParameter(java.lang.Object)
     */
    public String generateParameter(Object handle) {
	return "Parameter(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generatePackage(java.lang.Object)
     */
    public String generatePackage(Object handle) {
	return "Package(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateClassifier(java.lang.Object)
     */
    public String generateClassifier(Object handle) {
	return "Classifier(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateTaggedValue(java.lang.Object)
     */
    public String generateTaggedValue(Object handle) {
	return "TaggedValue(" + ModelFacade.getTag(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateAssociation(java.lang.Object)
     */
    public String generateAssociation(Object handle) {
	return "Association(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateAssociationEnd(java.lang.Object)
     */
    public String generateAssociationEnd(Object handle) {
	return "AssociationEnd(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateAssociationRole(java.lang.Object)
     */
    public String generateAssociationRole(Object handle) {
	return "AssociationRole(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateMultiplicity(java.lang.Object)
     */
    public String generateMultiplicity(Object handle) {
	return "Multiplicity(" + handle.toString() + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateVisibility(java.lang.Object)
     */
    public String generateVisibility(Object handle) {
        if (ModelFacade.isAVisibilityKind(handle)) {
            return "Visibility(" + ModelFacade.getValue(handle) + ")";
        }
        return "Visibility(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateState(java.lang.Object)
     */
    public String generateState(Object handle) {
	return "State(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateStateBody(java.lang.Object)
     */
    public String generateStateBody(Object handle) {
	return "StateBody(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateTransition(java.lang.Object)
     */
    public String generateTransition(Object handle) {
	return "Transition(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateAction(java.lang.Object)
     */
    public String generateAction(Object handle) {
	if (ModelFacade.isAAction(handle)) {
	    return "Action(" + ModelFacade.getName(handle) + ")";
	}
	return "Action(" + handle.toString() + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateGuard(java.lang.Object)
     */
    public String generateGuard(Object handle) {
	return "Guard(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateMessage(java.lang.Object)
     */
    public String generateMessage(Object handle) {
	return "Message(" + ModelFacade.getName(handle) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateObjectFlowState(java.lang.Object)
     */
    public String generateObjectFlowState(Object m) {
        Object c = ModelFacade.getType(m);
        if (c == null) {
            return "";
        }
        return ModelFacade.getName(c);
    }    

    /**
     * @see org.argouml.uml.generator.Generator2#generateEvent(java.lang.Object)
     */
    public String generateEvent(Object m) {
        return "Event(" + ModelFacade.getName(m) + ")";
    }

    /**
     * @see org.argouml.application.api.NotationProvider2#generateActionState(java.lang.Object)
     */
    public String generateActionState(Object actionState) {
        return "ActionState(" + ModelFacade.getName(actionState) + ")";
    }


    /**
     * @see org.argouml.application.api.ArgoModule#getModuleName()
     */
    public String getModuleName() { return "GeneratorDummy"; }

    /**
     * @see org.argouml.application.api.ArgoModule#getModuleDescription()
     */
    public String getModuleDescription() {
        return "Dummy language";
    }

    /**
     * @see org.argouml.application.api.ArgoModule#getModuleAuthor()
     */
    public String getModuleAuthor() {
        return "ArgoUML Dummy Language addition";
    }

    /**
     * @see org.argouml.application.api.ArgoModule#getModuleVersion()
     */
    public String getModuleVersion() { return "0.9.9"; }

    /**
     * @see org.argouml.application.api.ArgoModule#getModuleKey()
     */
    public String getModuleKey() {
	return "module.language.generator.dummylanguage";
    }
} /* end class GeneratorJava */

