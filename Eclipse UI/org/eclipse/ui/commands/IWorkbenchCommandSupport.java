package org.eclipse.ui.commands;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.internal.keys.WorkbenchKeyboard;

/**
 * An instance of this interface provides support for managing commands at the
 * <code>IWorkbench</code> level.
 * <p>
 * This interface is not intended to be extended or implemented by clients.
 * </p>
 * <p>
 * <em>EXPERIMENTAL</em>
 * </p>
 * 
 * @since 3.0
 * @see IWorkbench#getAdaptable
 */
public interface IWorkbenchCommandSupport {

	/**
	 * Deregisters the given <code>shell</code> from the global key bindings.
	 * This is not strictly necessary (as the internal storage uses a weak hash
	 * map), but is good for cleanliness.
	 * 
	 * @param shell
	 *            The shell to deregister; may be <code>null</code>.
	 */
	void deregisterFromKeyBindings(Shell shell);

	/**
	 * Disables the global key binding architecture. This means that no key
	 * events will trapped as keyboard shortcuts, and that no commands can be
	 * triggered by keyboard events. (Exception: it is possible that someone
	 * listening for key events on a widget could trigger a command.)
	 */
	void disableKeyFilter();

	/**
	 * Enables the global key binding architecture. The architecture should be
	 * enabled by default. This means that keyboard shortcuts are active, and
	 * that key events can trigger commands. This also means that widgets may
	 * not see all key events (as they might be trapped as a keyboard
	 * shortcut).
	 */
	void enableKeyFilter();

	/**
	 * Returns the command manager for the workbench.
	 * 
	 * @return the command manager for the workbench. Guaranteed not to be
	 *         <code>null</code>.
	 */
	ICommandManager getCommandManager();

	/**
	 * Returns the compound command handler service for the workbench.
	 * 
	 * @return the compound command handler service for the workbench.
	 *         Guaranteed not to be <code>null</code>.
	 */
	ICompoundCommandHandlerService getCompoundCommandHandlerService();

	/**
	 * An accessor for the keyboard interface this workbench is using. This can
	 * be used by external class to get a reference with which they can
	 * simulate key presses in the key binding architecture. This is used for
	 * testing purposes currently.
	 * 
	 * @return A reference to the workbench keyboard interface; never <code>null</code>.
	 */
	public WorkbenchKeyboard getKeyboard();

	/**
	 * Tests whether the global key binding architecture is currently active.
	 * 
	 * @return <code>true</code> if the key bindings are active; <code>false</code>
	 *         otherwise.
	 */
	boolean isKeyFilterEnabled();

	/**
	 * Indicates that the given <code>shell</code> wishes to participate in
	 * the global key binding architecture. This means that key and traversal
	 * events might be intercepted before reaching any of the widgets on the
	 * shell.
	 * 
	 * @param shell
	 *            The shell to register for key bindings; may be <code>null</code>.
	 * @param dialogOnly
	 *            Whether the shell only wants the restricted set of key
	 *            bindings normally used in dialogs (e.g., text editing
	 *            commands). All workbench windows say <code>false</code>
	 *            here.
	 */
	void registerForKeyBindings(Shell shell, boolean dialogOnly);
}
