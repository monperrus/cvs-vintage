// Copyright (c) 1996-99 The Regents of the University of California. All
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

// File: MMUtil.java
// Classes: MMUtil
// Original Author: not known

// 3 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// the Extend and Include relationships.

// 8 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to add
// buildExtensionPoint and getExtensionPoints methods for use cases.

// 16 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to remove
// include and extend relationships when deleting a use case.


package org.argouml.uml;

import ru.novosoft.uml.*;
import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.model_management.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;
import ru.novosoft.uml.behavior.use_cases.*;
import ru.novosoft.uml.behavior.collaborations.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.behavior.common_behavior.*;

import java.util.*;

import org.argouml.ui.ProjectBrowser;
import org.apache.log4j.Category;
import org.argouml.application.api.Argo;
import org.argouml.application.api.Notation;
import org.argouml.application.api.NotationName;
import org.argouml.application.notation.NotationProviderFactory;
import org.argouml.kernel.Project;
import org.argouml.model.uml.UmlFactory;

public class MMUtil {

	Category cat = Category.getInstance(org.argouml.uml.MMUtil.class);

	public static MMUtil SINGLETON = new MMUtil();

	public static MModel STANDARDS;

	static {
		STANDARDS = UmlFactory.getFactory().getModelManagement().createModel();
		STANDARDS.setName("standard Elements");
		MStereotype realizationStereo = UmlFactory.getFactory().getExtensionMechanisms().createStereotype();
		realizationStereo.setName("realize");
		STANDARDS.addOwnedElement(realizationStereo);

		MStereotype interfaceStereo = UmlFactory.getFactory().getExtensionMechanisms().createStereotype();
		interfaceStereo.setName("interface");
		STANDARDS.addOwnedElement(interfaceStereo);
	}

    /**
     * <p>Remove a use case and its associated connections from the model.<p>
     *
     * <p>We remove the include and extend here, then use the classifier
     *   version of this method to remove everything else. Note that we must
     *   get rid of both ends.</p>
     *
     * @param useCase  The use case to remove form the model
     */

    public void remove(MUseCase useCase) {

        // Get rid of extends

        Iterator extendIterator = (useCase.getExtends()).iterator();

        while (extendIterator.hasNext()) {
            ((MExtend) extendIterator.next()).remove();
        }

        extendIterator = (useCase.getExtends2()).iterator();

        while (extendIterator.hasNext()) {
            ((MExtend) extendIterator.next()).remove();
        }

        // Get rid of includes

        Iterator includeIterator = (useCase.getIncludes()).iterator();

        while (includeIterator.hasNext()) {
            ((MInclude) includeIterator.next()).remove();
        }

        includeIterator = (useCase.getIncludes2()).iterator();

        while (includeIterator.hasNext()) {
            ((MInclude) includeIterator.next()).remove();
        }

        // Use the classifier version to get rid of everything else (including
        // our very own good selves).

        remove((MClassifier) useCase);
    }

    // This method takes care about removing all unneeded transitions
    // while removing a StateVertex (like a State or ActionState, also Fork et.al.)
    public void remove(MStateVertex sv) {
	Collection transitions = sv.getIncomings();
	Iterator transitionIterator = transitions.iterator();
	while (transitionIterator.hasNext()) {
	    MTransition transition = (MTransition)transitionIterator.next();
	    transition.remove();
	}
	transitions = sv.getOutgoings();
	transitionIterator = transitions.iterator();
	while (transitionIterator.hasNext()) {
	    MTransition transition = (MTransition)transitionIterator.next();
	    transition.remove();
	}
	sv.remove();
    }
    
    /**
     * Removes a modelelement. Delegates the actual removal to more 
     * specific methods if specific elements must be removed.
     * @param me
     * @see org.argouml.kernel.Project#trashInternal(Object) for the use of the method
     * @author jaap.branderhorst@xs4all.nl
     */
    public void remove(MModelElement me) {
    	if (me instanceof MClassifier) {
    		remove((MClassifier)me); return;
    	}
    	if (me instanceof MStateVertex) {
    		remove((MStateVertex)me); return;
    	}
    	if (me instanceof MLink) {
    		remove((MLink)me); return;
    	}
    	if (me instanceof MObject) {
    		remove((MObject)me); return;
    	}
    	if (me instanceof MStimulus) {
    		remove((MStimulus)me); return;
    	}
    	if (me instanceof MUseCase) {
    		remove((MUseCase)me); return;
    	}
    	me.remove();
    }

    /**
     * Removes a classifier including all depending Modelelements.
     * @param cls The classifier to be removed
     */
	// This method takes care about removing all unneeded associations,
	// generalizations, ClassifierRoles and dependencies when removing
	// a classifier.
    public void remove(MClassifier cls) {
		Iterator ascEndIterator = (cls.getAssociationEnds()).iterator();
		while (ascEndIterator.hasNext()) {
			MAssociationEnd ae = (MAssociationEnd)ascEndIterator.next();
			MAssociation assoc = ae.getAssociation();
			if ((assoc.getConnections()).size() < 3)
				assoc.remove();
			else
				ae.remove();
		}

		Iterator roleIterator = (cls.getClassifierRoles()).iterator();
		while (roleIterator.hasNext()) {
			MClassifierRole r = (MClassifierRole)roleIterator.next();
			r.remove();
		}

		Iterator generalizationIterator = (cls.getGeneralizations()).iterator();
		while (generalizationIterator.hasNext()) {
			MGeneralization gen = (MGeneralization)generalizationIterator.next();
			gen.remove();
		}

		Iterator specializationIterator = (cls.getSpecializations()).iterator();
		while (specializationIterator.hasNext()) {
			MGeneralization spec = (MGeneralization)specializationIterator.next();
			spec.remove();
		}

		Iterator clientDependencyIterator = cls.getClientDependencies().iterator();
		while (clientDependencyIterator.hasNext()) {
			MDependency dep = (MDependency)clientDependencyIterator.next();
			if (dep.getClients().size() < 2)
				dep.remove();
		}

		Iterator supplierDependencyIterator = cls.getSupplierDependencies().iterator();
		while (supplierDependencyIterator.hasNext()) {
			MDependency dep = (MDependency)supplierDependencyIterator.next();
			if (dep.getSuppliers().size() < 2)
				dep.remove();
		}


		cls.remove(); //takes also care of removing the elementlisteners
    }

 public void remove (MObject obj) {
	Iterator linkEndIterator = (obj.getLinkEnds()).iterator();
	while (linkEndIterator.hasNext()) {
	    MLinkEnd le = (MLinkEnd)linkEndIterator.next();
	    MLink link = le.getLink();
	    if ((link.getConnections()).size() < 3)
		link.remove();
	    else
		le.remove();
	}
	obj.remove();
    }

    public void remove (MStimulus stimulus) {
	MLink link = stimulus.getCommunicationLink();
	link.remove();
	stimulus.remove();
    }

     public void remove (MLink link) {
	link.remove();
    }
    
	/** This method returns all Interfaces of which this class is a realization.
	 * @param cls  the class you want to have the interfaces for
	 * @return a collection of the Interfaces
	 */

	public Collection getSpecifications(MClassifier cls) {

		Collection result = new Vector();
		Collection deps = cls.getClientDependencies();
		Iterator depIterator = deps.iterator();

		while (depIterator.hasNext()) {
			MDependency dep = (MDependency)depIterator.next();
			if ((dep instanceof MAbstraction) &&
			    dep.getStereotype() != null &&
			    dep.getStereotype().getName() != null &&
			    dep.getStereotype().getName().equals("realize")) {
			    MInterface i = (MInterface)dep.getSuppliers().toArray()[0];
			    result.add(i);
			}
		}
		return result;
	}

	/** This method returns all Classifiers of which this class is a 
	 *	direct subtype.
	 *
	 * @param cls  the class you want to have the parents for
	 * @return a collection of the parents, each of which is a 
	 *					{@link MGeneralizableElement MGeneralizableElement}
	 */
	public Collection getSupertypes(MClassifier cls) {

		Collection result = new HashSet();
		Collection gens = cls.getGeneralizations();
		Iterator genIterator = gens.iterator();

		while (genIterator.hasNext()) {
			MGeneralization next = (MGeneralization) genIterator.next();
			result.add(next.getParent());
		}
		return result;
	}
	
	/** This method returns all Classifiers of which this class is a 
	 *	direct or indirect subtype.
	 *
	 * @param cls  the class you want to have the parents for
	 * @return a collection of the parents, each of which is a 
	 *					{@link MGeneralizableElement MGeneralizableElement}
	 */
	public Collection getAllSupertypes(MClassifier cls) {

		Collection result = new HashSet();

		Collection add = getSupertypes(cls);
		do 
		{
			Collection newAdd = new HashSet();
			Iterator addIter = add.iterator();
			while (addIter.hasNext())
			{
				MGeneralizableElement next = (MGeneralizableElement) addIter.next();
				if (next instanceof MClassifier) 
				{
					newAdd.addAll( getSupertypes((MClassifier) next) );
				}
			}
			result.addAll(add);
			add = newAdd;
			add.removeAll(result);
		}
		while (! add.isEmpty());
		
		return result;
	}
	

	/** This method returns all Classifiers of which this class is a 
	 *	direct supertype.
	 *
	 * @param cls  the class you want to have the children for
	 * @return a collection of the children, each of which is a 
	 *					{@link MGeneralizableElement MGeneralizableElement}
	 */
	public Collection getSubtypes(MClassifier cls) {

		Collection result = new Vector();
		Collection gens = cls.getSpecializations();
		Iterator genIterator = gens.iterator();

		while (genIterator.hasNext()) {
			MGeneralization next = (MGeneralization) genIterator.next();
			result.add(next.getChild());
		}
		return result;
	}

	/** This method returns all attributes of a given Classifier.
	 *
	 * @param classifier the classifier you want to have the attributes for
	 * @return a collection of the attributes
	 */

	public Collection getAttributes(MClassifier classifier) {
	    Collection result = new ArrayList();
		Iterator features = classifier.getFeatures().iterator();
		while (features.hasNext()) {
			MFeature feature = (MFeature)features.next();
			if (feature instanceof MAttribute)
				result.add(feature);
		}
		return result;
	}

	/** This method returns all opposite AssociationEnds of a given Classifier
	 *
	 * @param classifier the classifier you want to have the opposite association ends for
	 * @return a collection of the opposite associationends
	 */
	public Collection getAssociateEnds(MClassifier classifier) {
	    Collection result = new ArrayList();
		Iterator ascends = classifier.getAssociationEnds().iterator();
		while (ascends.hasNext()) {
			MAssociationEnd ascend = (MAssociationEnd)ascends.next();
			if ((ascend.getOppositeEnd() != null))
				result.add(ascend.getOppositeEnd());
		}
		return result;
	}

	/** This method returns all operations of a given Classifier
	 *
	 * @param classifier the classifier you want to have the operations for
	 * @return a collection of the operations
	 */
	public Collection getOperations(MClassifier classifier) {
	    Collection result = new ArrayList();
		Iterator features = classifier.getFeatures().iterator();
		while (features.hasNext()) {
			MFeature feature = (MFeature)features.next();
			if (feature instanceof MOperation)
				result.add(feature);
		}
		return result;
	}

	/** This method returns all attributes of a given Classifier, including inherited
	 *
	 * @param classifier the classifier you want to have the attributes for
	 * @return a collection of the attributes
	 */

	public Collection getAttributesInh(MClassifier classifier) {
	    Collection result = new ArrayList();
		result.addAll(getAttributes(classifier));
		Iterator parents = classifier.getParents().iterator();
		while (parents.hasNext()) {
			MClassifier parent = (MClassifier)parents.next();
  			System.out.println("Adding attributes for: "+parent);
			result.addAll(getAttributesInh(parent));
		}
		return result;
	}

	/** This method returns all opposite AssociationEnds of a given Classifier, including inherited
	 *
	 * @param classifier the classifier you want to have the opposite association ends for
	 * @return a collection of the opposite associationends
	 */
	public Collection getAssociateEndsInh(MClassifier classifier) {
	    Collection result = new ArrayList();
		result.addAll(getAssociateEnds(classifier));
		Iterator parents = classifier.getParents().iterator();
		while (parents.hasNext()) {
		    result.addAll(getAssociateEndsInh((MClassifier)parents.next()));
		}
		return result;
	}

	/** This method returns all operations of a given Classifier, including inherited
	 *
	 * @param classifier the classifier you want to have the operations for
	 * @return a collection of the operations
	 */
	public Collection getOperationsInh(MClassifier classifier) {
	    Collection result = new ArrayList();
		result.addAll(getOperations(classifier));
		Iterator parents = classifier.getParents().iterator();
		while (parents.hasNext()) {
			result.addAll(getOperationsInh((MClassifier)parents.next()));
		}
		return result;
	}


    /**
     * Returns all return parameters for an operation.
     * @param operation
     * @return Collection
     */
	public Collection getReturnParameters(MOperation operation) {
		Vector returnParams = new Vector();
		MParameter firstReturnParameter = null;
		Iterator params = operation.getParameters().iterator();
		while (params.hasNext()) {
			MParameter parameter = (MParameter)params.next();
			if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
				returnParams.add(parameter);
			}
		}
		return (Collection)returnParams;
	}
	
	/** this method finds all paramters of the given operation which have
	 * the MParamterDirectionType RETURN. If it is only one, it is returned.
	 * In case there are no return parameters, null is returned. If there
	 * is more than one return paramter, first of them is returned, but a
	 * message is written to System.out
	 *
	 * @param operation the operation you want to find the return parameter for
	 * @return If this operation has only one paramter with Kind: RETURN, this is it, otherwise null
	 */

	public MParameter getReturnParameter(MOperation operation) {
		Vector returnParams = new Vector();
		MParameter firstReturnParameter = null;
		Iterator params = operation.getParameters().iterator();
		while (params.hasNext()) {
			MParameter parameter = (MParameter)params.next();
			if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
				returnParams.add(parameter);
			}
		}

		switch (returnParams.size()) {
		case 1:
			return (MParameter)returnParams.elementAt(0);
		case 0:
		    // System.out.println("No ReturnParameter found!");
			return null;
		default:
			System.out.println("More than one ReturnParameter found, returning first!");
			return (MParameter)returnParams.elementAt(0);
		}
	}

    /**
     * Build a returnparameter. Removes all current return parameters from the
     * operation and adds the supplied parameter. The directionkind of the 
     * parameter will be return. The name will be equal to the name of the last
     * found return parameter or the default value "return" if no return
     * parameter was present in the operation.
     * @param operation
     * @param newReturnParameter
     */
	public void setReturnParameter(MOperation operation, MParameter newReturnParameter) {
		Iterator params = operation.getParameters().iterator();
        String name = "return";
		while (params.hasNext()) {
			MParameter parameter = (MParameter)params.next();
			if ((parameter.getKind()).equals(MParameterDirectionKind.RETURN)) {
				operation.removeParameter(parameter);
                if (parameter.getName() != null || parameter.getName() == "") {
                    name = parameter.getName();
                }
			}
		}
        newReturnParameter.setName(name);
		newReturnParameter.setKind(MParameterDirectionKind.RETURN);
		operation.addParameter(0, newReturnParameter);
	}


    /** 
     * <p>This method returns all extension points of a given use case.
     *
     * <p>Here for completeness, but actually just a wrapper for the NSUML
     *   function.</p>
     *
     * @param useCase  The use case for which we want the extension points.
     *
     * @return         A collection of the extension points.
     */

    public Collection getExtensionPoints(MUseCase useCase) {

        return useCase.getExtensionPoints();
    }
    
    
}

