package org.eclipse.jdt.internal.compiler.codegen;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jdt.internal.compiler.lookup.*;

/**
 * This type is a port of smalltalks JavaLabel
 */
public class Label {
	public CodeStream codeStream;
	final static int POS_NOT_SET = -1;
	public int position = POS_NOT_SET; // iPOS=POS_NOT_SET Then it's pos is not set.
	int[] forwardReferences = new int[10]; // Add an overflow check here.
	int forwardReferenceCount = 0;
public Label() {
}
/**
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 */
public Label(CodeStream codeStream) {
	this.codeStream = codeStream;
}
/**
 * Add a forward refrence for the array.
 */
void addForwardReference(int iPos) {
	int length;
	if (forwardReferenceCount >= (length = forwardReferences.length))
		System.arraycopy(forwardReferences, 0, (forwardReferences = new int[2*length]), 0, length);
	forwardReferences[forwardReferenceCount++] = iPos;
}
/**
 * Add a forward refrence for the array.
 */
void appendForwardReferencesFrom(Label otherLabel) {
	int otherCount = otherLabel.forwardReferenceCount;
	if (otherCount == 0) return;
	int length = forwardReferences.length;
	int neededSpace = otherCount + forwardReferenceCount;
	if (neededSpace >= length){
		System.arraycopy(forwardReferences, 0, (forwardReferences = new int[neededSpace]), 0, forwardReferenceCount);
	}
	// append other forward references at the end, so they will get updated as well
	System.arraycopy(otherLabel.forwardReferences, 0, forwardReferences, forwardReferenceCount, otherCount);
	forwardReferenceCount = neededSpace;
}
/*
* Put down  a refernece to the array at the location in the codestream.
*/
void branch() {
	if (position == POS_NOT_SET) {
		addForwardReference(codeStream.position);
		// Leave two bytes free to generate the jump afterwards
		codeStream.position += 2;
		codeStream.classFileOffset += 2;
	} else { //Position is set. Write it!
		codeStream.writeSignedShort((short) (position - codeStream.position + 1));
	}
}
/*
* No support for wide branches yet
*/
void branchWide() {
	if (position == POS_NOT_SET) {
		addForwardReference(codeStream.position);
		// Leave 4 bytes free to generate the jump offset afterwards
		codeStream.position += 4;
		codeStream.classFileOffset += 4;
	} else { //Position is set. Write it!
		codeStream.writeSignedWord(position - codeStream.position + 1);
	}
}
/**
 * @return boolean
 */
public boolean hasForwardReferences() {
	return forwardReferenceCount != 0;
}
/*
 * Some placed labels might be branching to a goto bytecode which we can optimize better.
 */
public void inlineForwardReferencesFromLabelsTargeting(int gotoLocation) {
/*
 Code required to optimized unreachable gotos.
	public boolean isBranchTarget(int location) {
		Label[] labels = codeStream.labels;
		for (int i = codeStream.countLabels - 1; i >= 0; i--){
			Label label = labels[i];
			if ((label.position == location) && label.isStandardLabel()){
				return true;
			}
		}
		return false;
	}
 */
	
	Label[] labels = codeStream.labels;
	for (int i = codeStream.countLabels - 1; i >= 0; i--){
		Label label = labels[i];
		if ((label.position == gotoLocation) && label.isStandardLabel()){
			this.appendForwardReferencesFrom(label);
			/*
			 Code required to optimized unreachable gotos.
				label.position = POS_NOT_SET;
			*/
		} else {
			break; // same target labels should be contiguous
		}
	}
}
public boolean isStandardLabel(){
	return true;
}
/*
* Place the label. If we have forward references resolve them.
*/
public void place() { // Currently lacking wide support.
	if (position == POS_NOT_SET) {
		position = codeStream.position;
		codeStream.addLabel(this);
		int oldPosition = position;
		boolean optimizedBranch = false;
		// TURNED OFF since fail on 1F4IRD9
		if (forwardReferenceCount != 0) {
			if (optimizedBranch = (forwardReferences[forwardReferenceCount - 1] + 2 == position) && (codeStream.bCodeStream[codeStream.classFileOffset - 3] == CodeStream.OPC_goto)) {
				codeStream.position = (position -= 3);
				codeStream.classFileOffset -= 3;
				forwardReferenceCount--;
				// also update the PCs in the related debug attributes
				/** OLD CODE
					int index = codeStream.pcToSourceMapSize - 1;
						while ((index >= 0) && (codeStream.pcToSourceMap[index][1] == oldPosition)) {
							codeStream.pcToSourceMap[index--][1] = position;
						}
				*/
				// Beginning of new code
				int index = codeStream.pcToSourceMapSize - 2;
				if (codeStream.lastEntryPC == oldPosition) {
					codeStream.lastEntryPC = position;
				}
				if ((index >= 0) && (codeStream.pcToSourceMap[index] == position)) {
					codeStream.pcToSourceMapSize-=2;
				}
				// end of new code
				if (codeStream.generateLocalVariableTableAttributes) {
					LocalVariableBinding locals[] = codeStream.locals;
					for (int i = 0, max = locals.length; i < max; i++) {
						LocalVariableBinding local = locals[i];
						if ((local != null) && (local.initializationCount > 0)) {
							if (local.initializationPCs[((local.initializationCount - 1) << 1) + 1] == oldPosition) {
								local.initializationPCs[((local.initializationCount - 1) << 1) + 1] = position;
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < forwardReferenceCount; i++) {
			codeStream.writeSignedShort(forwardReferences[i], (short) (position - forwardReferences[i] + 1));
		}
		// For all labels placed at that position we check if we need to rewrite the jump
		// offset. It is the case each time a label had a forward reference to the current position.
		// Like we change the current position, we have to change the jump offset. See 1F4IRD9 for more details.
		if (optimizedBranch) {
			for (int i = 0; i < codeStream.countLabels; i++) {
				Label label = codeStream.labels[i];
				if (oldPosition == label.position) {
					label.position = position;
					if (label instanceof CaseLabel) {
						int offset = position - ((CaseLabel) label).instructionPosition;
						for (int j = 0; j < label.forwardReferenceCount; j++) {
							int forwardPosition = label.forwardReferences[j];
							codeStream.writeSignedWord(forwardPosition, offset);
						}
					} else {
						for (int j = 0; j < label.forwardReferenceCount; j++) {
							int forwardPosition = label.forwardReferences[j];
							codeStream.writeSignedShort(forwardPosition, (short) (position - forwardPosition + 1));
						}
					}
				}
			}
		}
	}
}
/**
 * Print out the receiver
 */
public String toString() {
	StringBuffer buffer = new StringBuffer("(position="/*nonNLS*/);
	buffer.append(position);
	buffer.append(", forwards = ["/*nonNLS*/);
	for (int i = 0; i < forwardReferenceCount - 1; i++)
		buffer.append(forwardReferences[i] + ", "/*nonNLS*/);
	if (forwardReferenceCount >= 1)
		buffer.append(forwardReferences[forwardReferenceCount-1]);
	buffer.append("] )"/*nonNLS*/);
	return buffer.toString();
}
}
