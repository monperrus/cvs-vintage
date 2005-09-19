// $Id: FigTransition.java,v 1.42 2005/09/19 17:14:06 mvw Exp $
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
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

package org.argouml.uml.diagram.state.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;

import org.argouml.application.api.Notation;
import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.Model;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.PathConvPercent2;
import org.argouml.uml.generator.ParserDisplay;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.ArrowHeadGreater;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigText;

/**
 * This class represents the graphical representation of a transition
 * on a diagram.
 *
 */
public class FigTransition extends FigEdgeModelElement {

    private ArrowHeadGreater endArrow = new ArrowHeadGreater();

    /**
     * If <code>dashed</code> is true, then the transition represents
     * "object flow".
     * If the line is solid, then it represents "control flow".
     */
    private boolean dashed = false;

    ////////////////////////////////////////////////////////////////
    // constructors
    /**
     * The main constructor.
     */
    public FigTransition() {
        super();
        addPathItem(getNameFig(), new PathConvPercent2(this, getNameFig(), 50, 10));
        _fig.setLineColor(Color.black);
        setDestArrowHead(endArrow);
        allowRemoveFromDiagram(false);
    }

    /**
     * The constructor that hooks the Fig into an existing UML element.
     *
     * It also adapts the line to be dashed if the source or destination
     * is an ObjectFlowState.
     *
     * @param edge the UML element
     * @param lay the layer
     */
    public FigTransition(Object edge, Layer lay) {
        this();
        if (Model.getFacade().isATransition(edge)) {
            Object tr = /* (MTransition) */edge;
            Object sourceSV = Model.getFacade().getSource(tr);
            Object destSV = Model.getFacade().getTarget(tr);
            FigNode sourceFN = (FigNode) lay.presentationFor(sourceSV);
            FigNode destFN = (FigNode) lay.presentationFor(destSV);
            setSourcePortFig(sourceFN);
            setSourceFigNode(sourceFN);
            setDestPortFig(destFN);
            setDestFigNode(destFN);

            dashed =
                Model.getFacade().isAObjectFlowState(sourceSV)
                    || Model.getFacade().isAObjectFlowState(destSV);
        }
        setLayer(lay);
        setOwner(edge);
    }

    /**
     * The constructor that hooks the Fig into an existing UML element.
     *
     * @param edge the UML element
     */
    public FigTransition(Object edge) {
        this(edge, ProjectManager.getManager().getCurrentProject()
                .getActiveDiagram().getLayer());
    }

    ////////////////////////////////////////////////////////////////
    // event handlers

    /**
     * @see org.tigris.gef.presentation.FigEdge#setFig(org.tigris.gef.presentation.Fig)
     */
    public void setFig(Fig f) {
        super.setFig(f);
        _fig.setDashed(dashed);
    }

    /**
     * This method is called after the user finishes editing a text field that
     * is in the FigEdgeModelElement. Determine which field and update the
     * model. This class handles the name, subclasses should override to handle
     * other text elements.
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#textEdited(org.tigris.gef.presentation.FigText)
     */
    protected void textEdited(FigText ft) {
        Object t = /* (MTransition) */getOwner();
        if (t == null) {
            return;
        }
        String s = ft.getText();
        try {
            ParserDisplay.SINGLETON.parseTransition(t, s);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.transition";
            Object[] args = {
                pe.getLocalizedMessage(),
                new Integer(pe.getErrorOffset()),
            };
            ProjectBrowser.getInstance().getStatusBar().showStatus(
                    Translator.messageFormat(msg, args));
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#textEditStarted(org.tigris.gef.presentation.FigText)
     */
    protected void textEditStarted(FigText ft) {
        if (ft == getNameFig()) {
            showHelp("parsing.help.fig-transition");
        }
    }

    /**
     * This is called after any part of the UML ModelElement has changed. This
     * method automatically updates the name FigText. Subclasses should override
     * and update other parts.
     *
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#modelChanged(java.beans.PropertyChangeEvent)
     */
    protected void modelChanged(PropertyChangeEvent e) {
        super.modelChanged(e);
        if (e == null) {
            return;
        }

        // register the guard condition
        if (Model.getFacade().isATransition(e.getSource())
                && (e.getSource() == getOwner()
                        && e.getPropertyName().equals("guard"))) {
            Model.getPump().addModelEventListener(this,
                    e.getNewValue(), "expression");
            updateNameText();
            damage();
        } else if (Model.getFacade().isATransition(e.getSource())
                && e.getSource() == getOwner()
                && e.getPropertyName().equals("trigger")) {
            // register the event (or trigger)
            Model.getPump().addModelEventListener(this,
                    e.getNewValue(), new String[] {
                    	"parameter", "name",
                        //TODO: How to listen to time/change expression?
            	    });
            updateNameText();
            damage();
        } else if (Model.getFacade().isATransition(e.getSource())
                && e.getSource() == getOwner()
                && e.getPropertyName().equals("effect")) {
            // register the action
            Model.getPump().addModelEventListener(this,
                    e.getNewValue(), "script");
            updateNameText();
            damage();
        } else if (Model.getFacade().isAEvent(e.getSource())
                && Model.getFacade().getTransitions(e.getSource()).contains(
                        getOwner())) {
            // handle events send by the event
//            if (e.getPropertyName().equals("parameter")) {
//                // TODO: When does this ever get used? How to replace?
//                if (e.getAddedValue() != null) {
//                    Model.getPump().addModelEventListener(
//                            this,
//                            e.getAddedValue());
//                } else if (e.getRemovedValue() != null) {
//                    Model.getPump().removeModelEventListener(
//                            this,
//                            e.getRemovedValue());
//                }
//            }
            updateNameText();
            damage();
        } else if (Model.getFacade().isAGuard(e.getSource())) {
            // handle events send by the guard
            updateNameText();
            damage();
        } else if (Model.getFacade().isAAction(e.getSource())) {
            // handle events send by the action-effect
            updateNameText();
            damage();
        } else if (Model.getFacade().isAParameter(e.getSource())) {
            // handle events send by the parameters of the event
            updateNameText();
            damage();
        } else if ((e.getSource() == getOwner())
                && (e.getPropertyName().equals("source")
                        || (e.getPropertyName().equals("target")))) {
            dashed =
                Model.getFacade().isAObjectFlowState(getSource())
                || Model.getFacade().isAObjectFlowState(getDestination());
            _fig.setDashed(dashed);
        }
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#getDestination()
     */
    protected Object getDestination() {
        if (getOwner() != null) {
            return Model.getStateMachinesHelper().getDestination(
            /* (MTransition) */getOwner());
        }
        return null;
    }

    /**
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#getSource()
     */
    protected Object getSource() {
        if (getOwner() != null) {
            return Model.getStateMachinesHelper().getSource(
            /* (MTransition) */getOwner());
        }
        return null;
    }

    /**
     * @see org.tigris.gef.presentation.Fig#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        endArrow.setLineColor(getLineColor());
        super.paint(g);
    }

    /**
     * Updates the name text box. In case of a transition the name text box
     * contains:
     * <ul>
     * <li>The event-signature
     * <li>The guard condition between []
     * <li>The action expression
     * </ul>
     *
     * The contents of the text box is generated by the Generator
     *
     * @see org.argouml.uml.diagram.ui.FigEdgeModelElement#updateNameText()
     */
    protected void updateNameText() {
        getNameFig().setText(Notation.generate(this, getOwner()));
    }

} /* end class FigTransition */
