package org.columba.core.gui.contextualpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;

import org.columba.api.gui.frame.IDock;
import org.columba.api.gui.frame.IFrameMediator;
import org.columba.core.gui.contextualpanel.api.IContextualPanel;
import org.columba.core.gui.contextualpanel.api.IContextualProvider;
import org.columba.core.logging.Logging;
import org.jdesktop.swingx.VerticalLayout;

public class ContextualPanel extends JPanel implements IContextualPanel {

	private IFrameMediator frameMediator;

	private List<IContextualProvider> providerList = new Vector<IContextualProvider>();

	private StackedBox box;

	private JButton button;

	public ContextualPanel(IFrameMediator frameMediator) {
		super();

		this.frameMediator = frameMediator;

		button = new JButton("What's related");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		
		setLayout(new BorderLayout());

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.add(button, BorderLayout.CENTER);

		JPanel center = new JPanel();
		center.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		center.setLayout(new BorderLayout());

		box = new StackedBox();
		box.setBackground(UIManager.getColor("TextField.background"));

		JScrollPane pane = new JScrollPane(box);

		center.add(pane, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		
		if (Logging.DEBUG)
			register(new ContextDebugProvider());
	}

	private void showDockingView() {
		if (frameMediator instanceof IDock) {
			// show docking view
			((IDock) frameMediator)
					.showDockable(IDock.DOCKING_VIEW_CONTEXTUAL_PANEL);
		}

	}

	private void createStackedBox() {

		box.removeAll();

		Iterator<IContextualProvider> it = providerList.listIterator();
		while (it.hasNext()) {
			IContextualProvider p = it.next();
			ResultBox resultBox = new ResultBox(p);
			box.addBox(resultBox);
		}

		// repaint box
		validate();
		repaint();
	}

	public void search() {

		createStackedBox();

		showDockingView();

		Iterator<IContextualProvider> it = providerList.listIterator();
		while (it.hasNext()) {
			IContextualProvider p = it.next();
			p.search(frameMediator.getSemanticContext(), 0, 5);
		}
	}

	public JComponent getView() {
		return this;
	}

	public void register(IContextualProvider provider) {
		providerList.add(provider);
	}

	public void unregister(IContextualProvider provider) {
		providerList.remove(provider);
	}

	class StackedBox extends JPanel implements Scrollable {

		StackedBox() {
			setLayout(new VerticalLayout());
			setOpaque(true);

		}

		/**
		 * Adds a new component to this <code>StackedBox</code>
		 * 
		 * @param box
		 */
		public void addBox(JComponent box) {
			add(box);
		}

		/**
		 * @see Scrollable#getPreferredScrollableViewportSize()
		 */
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		/**
		 * @see Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int,
		 *      int)
		 */
		public int getScrollableBlockIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			return 10;
		}

		/**
		 * @see Scrollable#getScrollableTracksViewportHeight()
		 */
		public boolean getScrollableTracksViewportHeight() {
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
			} else {
				return false;
			}
		}

		/**
		 * @see Scrollable#getScrollableTracksViewportWidth()
		 */
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		/**
		 * @see Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int,
		 *      int)
		 */
		public int getScrollableUnitIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			return 10;
		}

	}
}
