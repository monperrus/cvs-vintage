/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jdt.internal.core;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Stack;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.util.JavaCompilationUnitSorter;
import org.eclipse.jdt.internal.compiler.SourceElementRequestorAdapter;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

/**
 * 
 * @since 2.1
 */
public class SortElementBuilder extends SourceElementRequestorAdapter {

	public static int getCategory(ASTNode node) {
		Hashtable options = JavaCore.getOptions();
		switch(node.getNodeType()) {
			case ASTNode.METHOD_DECLARATION :
				MethodDeclaration methodDeclaration= (MethodDeclaration) node;
				if (methodDeclaration.isConstructor()) {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_CONSTRUCTOR_ORDER));
				}
				int modifiers = methodDeclaration.getModifiers();
				if (Flags.isStatic(modifiers)) {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_STATIC_METHOD_ORDER));
				} else {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_METHOD_ORDER));
				}
			case ASTNode.FIELD_DECLARATION :
				modifiers = ((FieldDeclaration) node).getModifiers();
				if (Flags.isStatic(modifiers)) {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_STATIC_FIELD_ORDER));
				} else {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_FIELD_ORDER));
				}
			case ASTNode.TYPE_DECLARATION :
				return Integer.parseInt((String)options.get(JavaCore.SORTING_TYPE_ORDER));
			case ASTNode.INITIALIZER :
				modifiers = ((Initializer) node).getModifiers();
				if (Flags.isStatic(modifiers)) {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_STATIC_INITIALIZER_ORDER));
				} else {
					return Integer.parseInt((String)options.get(JavaCore.SORTING_INITIALIZER_ORDER));
				}
		}
		return 0;
	}
	
	abstract class SortElement extends SortJavaElement {
		SortElement(int sourceStart, int modifiers) {
			super(SortElementBuilder.this);
			this.sourceStart = normalizeSourceStart(sourceStart);
			this.modifiers = modifiers;
			this.children_count = 0;
			this.numberOfPreceedingBlankLines = 0;
		}

		public void dumpLineBreaks(int numberOfBlankLines, StringBuffer buffer, String lineSeparator) {
			for (int i = 0; i < numberOfBlankLines; i++) {
				buffer.append(lineSeparator);
			}
		}
		protected void setParameters(MethodDeclaration methodDeclaration, String[] parameterNames, String[] parameterTypes) {
			for (int i = 0, max = parameterNames.length; i < max; i++) {
				String type = parameterTypes[i];
				SingleVariableDeclaration singleVariableDeclaration = ast.newSingleVariableDeclaration();
				singleVariableDeclaration.setName(ast.newSimpleName(parameterNames[i]));
				int indexOfArrayBrace;
				if (type.indexOf('.') != -1) {
					String[] typeParts = splitOn('.', type);
					int length = typeParts.length;
					indexOfArrayBrace = typeParts[length - 1].indexOf('[');
					if (indexOfArrayBrace != -1) {
						int dimensions = occurencesOf('[', typeParts[length - 1]);
						typeParts[length - 1] = typeParts[length - 1].substring(0, indexOfArrayBrace);
						String[] typeSubstrings = new String[length];
						for (int j = 0; j < length; j++) {
							typeSubstrings[j] = new String(typeParts[j]);
						}
						singleVariableDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast.newName(typeSubstrings)), dimensions));
					} else {
						String[] typeSubstrings = new String[length];
						for (int j = 0; j < length; j++) {
							typeSubstrings[j] = new String(typeParts[j]);
						}
						singleVariableDeclaration.setType(ast.newSimpleType(ast.newName(typeSubstrings)));
					}
				} else if ((indexOfArrayBrace = type.indexOf('[')) != -1) {
					int dimensions = occurencesOf('[', type);
					type = type.substring(0, indexOfArrayBrace);
					singleVariableDeclaration.setType(ast.newArrayType(newType(type), dimensions));
				} else {
					singleVariableDeclaration.setType(newType(type));
				}
				methodDeclaration.parameters().add(singleVariableDeclaration);
			}
		}
			
		protected String[] splitOn(char divider, String stringToSplit) {
			int length = stringToSplit == null ? 0 : stringToSplit.length();
			if (length == 0)
				return new String[] { stringToSplit };
	
			int wordCount = 1;
			for (int i = 0; i < length; i++)
				if (stringToSplit.charAt(i) == divider)
					wordCount++;
			String[] split = new String[wordCount];
			int last = 0, currentWord = 0;
			for (int i = 0; i < length; i++) {
				if (stringToSplit.charAt(i) == divider) {
					split[currentWord++] = stringToSplit.substring(last, i);
					last = i + 1;
				}
			}
			split[currentWord] = stringToSplit.substring(last, length);
			return split;
		}
		
		protected int occurencesOf(char toBeFound, String s) {
			if (s == null) return 0;
			int count = 0;
			for (int i = 0, max = s.length(); i < max; i++)
				if (toBeFound == s.charAt(i))
					count++;
			return count;
		}
		
		protected Type newType(String type) {
			// check if type is a primitive type
			// TODO: remove the scanner
			scanner.setSource(type.toCharArray());
			scanner.resetTo(0, type.length());
			int token = 0;
			try {
				token = scanner.getNextToken();
			} catch(InvalidInputException e) {
				return null;
			}
			if (token == TerminalTokens.TokenNameIdentifier) {
				return ast.newSimpleType(ast.newSimpleName(new String(type)));
			} else {
				switch(token) {
					case TerminalTokens.TokenNameint :
						return ast.newPrimitiveType(PrimitiveType.INT);
					case TerminalTokens.TokenNamebyte :
						return ast.newPrimitiveType(PrimitiveType.BYTE);
					case TerminalTokens.TokenNameboolean :
						return ast.newPrimitiveType(PrimitiveType.BOOLEAN);
					case TerminalTokens.TokenNamechar :
						return ast.newPrimitiveType(PrimitiveType.CHAR);
					case TerminalTokens.TokenNamedouble :
						return ast.newPrimitiveType(PrimitiveType.DOUBLE);
					case TerminalTokens.TokenNamefloat :
						return ast.newPrimitiveType(PrimitiveType.FLOAT);
					case TerminalTokens.TokenNamelong :
						return ast.newPrimitiveType(PrimitiveType.LONG);
					case TerminalTokens.TokenNameshort :
						return ast.newPrimitiveType(PrimitiveType.SHORT);
					case TerminalTokens.TokenNamevoid :
						return ast.newPrimitiveType(PrimitiveType.VOID);
				}
			}
			return null;
		}

		abstract ASTNode convert();
	}
	
	abstract class SortAbstractMethodDeclaration extends SortElement {

		SortAbstractMethodDeclaration(int sourceStart, int modifiers, char[][] parametersNames, char[][] parametersTypes, char[][] thrownExceptions) {			
			super(sourceStart, modifiers);
			if (parametersNames != null) {
				int length = parametersNames.length;
				this.parametersNames = new String[length];
				this.parametersTypes = new String[length];
				for (int i = 0; i < length; i++) {
					this.parametersNames[i] = new String(parametersNames[i]);
					this.parametersTypes[i] = new String(parametersTypes[i]);
				}
			}
			if (thrownExceptions != null) {
				int length = thrownExceptions.length;
				this.thrownExceptions = new String[length];
				for (int i = 0; i < length; i++) {
					this.thrownExceptions[i] = new String(thrownExceptions[i]);
				}
			}

		}
		public String decodeSignature() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("("); //$NON-NLS-1$
			if (this.parametersNames != null) {
				int length = parametersNames.length;
				for (int i = 0; i < length - 1; i++) {
					buffer.append(parametersTypes[i] + " " + parametersNames[i] + ", "); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append(parametersTypes[length - 1] + " " + parametersNames[length - 1]); //$NON-NLS-1$
			}
			buffer.append(")"); //$NON-NLS-1$
			return buffer.toString();
		}

		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			if (this.numberOfPreceedingBlankLines != 0) {
				dumpLineBreaks(this.numberOfPreceedingBlankLines, buffer, lineSeparator);
			}
			int length = this.children_count;
			if (length != 0) {
				int start = this.sourceStart;
				int end = this.firstChildBeforeSorting.sourceStart - 1;

				for (int i = 0; i < length; i++) {
					buffer.append(SortElementBuilder.this.source, start, end - start + 1);
					this.children[i].generateSource(buffer, lineSeparator);
					if (i < length - 1) {
						start = this.children[i].sourceEnd + 1;
					} else {
						start = this.lastChildBeforeSorting.sourceEnd + 1;
					}
					if (i < length - 1) {
						end = this.children[i + 1].sourceStart - 1;
					} else {
						end = this.sourceEnd;
					}
				}
				buffer.append(SortElementBuilder.this.source, start, end - start + 1);
			} else {
				buffer.append(SortElementBuilder.this.source, this.sourceStart, this.sourceEnd - this.sourceStart + 1);
			}	
		}
	}
	
	class SortMethodDeclaration extends SortAbstractMethodDeclaration {
		SortMethodDeclaration(int sourceStart, int modifiers, char[] name, char[][] parametersNames, char[][] parametersTypes, char[][] thrownExceptions, char[] returnType) {			
			super(sourceStart, modifiers, parametersNames, parametersTypes, thrownExceptions);
			this.id = METHOD;
			this.name = new String(name);
			this.returnType = new String(returnType);
		}
		
		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("method ") //$NON-NLS-1$
				.append(name)
				.append(decodeSignature())
				.append(" " + returnType + LINE_SEPARATOR); //$NON-NLS-1$
		}
		
		ASTNode convert() {
			MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
			methodDeclaration.setConstructor(false);
			methodDeclaration.setModifiers(this.modifiers);
			methodDeclaration.setName(ast.newSimpleName(new String(this.name)));
			methodDeclaration.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));
			// set parameter names and types
			if (this.parametersNames != null) {
				setParameters(methodDeclaration, this.parametersNames, this.parametersTypes);
			}
			// set thrown exceptions
			if (this.thrownExceptions != null) {
				for (int j = 0, max2 = this.thrownExceptions.length; j < max2; j++) {
					String currentException = this.thrownExceptions[j];
					Name exceptionName;
					if (currentException.indexOf('.') == -1) {
						exceptionName = ast.newSimpleName(currentException);
					} else {
						exceptionName = ast.newName(splitOn('.', currentException));
					}
					methodDeclaration.thrownExceptions().add(exceptionName);
				}
			}
			// set return type
			int indexOfArrayBrace;
			String currentReturnType = this.returnType;
			if (currentReturnType.indexOf('.') != -1) {
				String[] returnTypeSubstrings = splitOn('.', currentReturnType);
				int length = returnTypeSubstrings.length;
				indexOfArrayBrace = returnTypeSubstrings[length - 1].indexOf('[');
				if (indexOfArrayBrace != -1) {
					int dimensions = occurencesOf('[', returnTypeSubstrings[length - 1]);
					returnTypeSubstrings[length - 1] = returnTypeSubstrings[length - 1].substring(0, indexOfArrayBrace);
					methodDeclaration.setReturnType(ast.newArrayType(ast.newSimpleType(ast.newName(returnTypeSubstrings)), dimensions));
				} else {
					methodDeclaration.setReturnType(ast.newSimpleType(ast.newName(returnTypeSubstrings)));
				}
			} else if ((indexOfArrayBrace = currentReturnType.indexOf('[')) != -1) {
				int dimensions = occurencesOf('[', currentReturnType);
				currentReturnType = currentReturnType.substring(0, indexOfArrayBrace);
				methodDeclaration.setReturnType(ast.newArrayType(newType(currentReturnType), dimensions));
			} else {
				methodDeclaration.setReturnType(newType(currentReturnType));
			}
			return methodDeclaration;				
		}
	}

	class SortConstructorDeclaration extends SortAbstractMethodDeclaration {
		SortConstructorDeclaration(int sourceStart, int modifiers, char[][] parametersNames, char[][] parametersTypes, char[][] thrownExceptions) {			
			super(sourceStart, modifiers, parametersNames, parametersTypes, thrownExceptions);
			this.id = CONSTRUCTOR;
		}

		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("constructor ") //$NON-NLS-1$
				.append(decodeSignature() + LINE_SEPARATOR);
		}
		
		ASTNode convert() {
			MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
			methodDeclaration.setConstructor(true);
			methodDeclaration.setModifiers(this.modifiers);
			methodDeclaration.setName(ast.newSimpleName(new String(this.name)));
			methodDeclaration.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));
			// set parameter names and types
			if (this.parametersNames != null) {
				setParameters(methodDeclaration, this.parametersNames, this.parametersTypes);
			}
			// set thrown exceptions
			if (this.thrownExceptions != null) {
				for (int j = 0, max2 = this.thrownExceptions.length; j < max2; j++) {
					String currentException = this.thrownExceptions[j];
					Name exceptionName;
					if (currentException.indexOf('.') == -1) {
						exceptionName = ast.newSimpleName(currentException);
					} else {
						exceptionName = ast.newName(splitOn('.', currentException));
					}
					methodDeclaration.thrownExceptions().add(exceptionName);
				}
			}
			return methodDeclaration;
		}			
	}
	
	public class SortFieldDeclaration extends SortElement {

		SortFieldDeclaration(int sourceStart, int modifiers, char[] type, char[] name, int nameSourceStart) {
			super(sourceStart, modifiers);
			this.declarationStart = sourceStart;
			this.id = FIELD;
			this.type = new String(type);
			this.name = new String(name);
			this.nameSourceStart = nameSourceStart;
		}

		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("field ") //$NON-NLS-1$
				.append(type + " " + name + LINE_SEPARATOR); //$NON-NLS-1$
		}
		
		ASTNode convert() {
			VariableDeclarationFragment variableDeclarationFragment = ast.newVariableDeclarationFragment();
			variableDeclarationFragment.setName(ast.newSimpleName(new String(this.name)));
			variableDeclarationFragment.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));
			FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(variableDeclarationFragment);

			String currentFieldType = this.type;
			
			int indexOfArrayBrace;
			if (currentFieldType.indexOf('.') != -1) {
				String[] typeParts = splitOn('.', currentFieldType);
				int length = typeParts.length;
				indexOfArrayBrace = typeParts[length - 1].indexOf('[');
				if (indexOfArrayBrace != -1) {
					int dimensions = occurencesOf('[', typeParts[length - 1]);
					typeParts[length - 1] = typeParts[length - 1].substring(0, indexOfArrayBrace);
					fieldDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast.newName(typeParts)), dimensions));
				} else {
					fieldDeclaration.setType(ast.newSimpleType(ast.newName(typeParts)));
				}
			} else if ((indexOfArrayBrace = currentFieldType.indexOf('[')) != -1) {
				int dimensions = occurencesOf('[', currentFieldType);
				currentFieldType = currentFieldType.substring(0, indexOfArrayBrace);
				fieldDeclaration.setType(ast.newArrayType(newType(currentFieldType), dimensions));
			} else {
				fieldDeclaration.setType(newType(currentFieldType));
			}
			fieldDeclaration.setModifiers(this.modifiers);
			return fieldDeclaration;
		}
		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			if (this.numberOfPreceedingBlankLines != 0) {
				dumpLineBreaks(this.numberOfPreceedingBlankLines, buffer, lineSeparator);
			}
			int length = this.children_count;
			if (length != 0) {
				int start = this.sourceStart;
				int end = this.firstChildBeforeSorting.sourceStart - 1;

				for (int i = 0; i < length; i++) {
					buffer.append(SortElementBuilder.this.source, start, end - start + 1);
					this.children[i].generateSource(buffer, lineSeparator);
					if (i < length - 1) {
						start = this.children[i].sourceEnd + 1;
					} else {
						start = this.lastChildBeforeSorting.sourceEnd + 1;
					}
					if (i < length - 1) {
						end = this.children[i + 1].sourceStart - 1;
					} else {
						end = this.declarationSourceEnd;
					}
				}
				buffer.append(SortElementBuilder.this.source, start, end - start + 1);
			} else {
				buffer.append(SortElementBuilder.this.source, this.sourceStart, this.declarationSourceEnd - this.sourceStart + 1);
			}	
		}
		protected void generateReduceSource(StringBuffer buffer, String lineSeparator) {
			int length = this.children_count;
			if (length != 0) {
				int start = this.nameSourceStart;
				int end = this.firstChildBeforeSorting.sourceStart - 1;
	
				for (int i = 0; i < length; i++) {
					buffer.append(SortElementBuilder.this.source, start, end - start + 1);
					this.children[i].generateSource(buffer, lineSeparator);
					if (i < length - 1) {
						start = this.children[i].sourceEnd + 1;
					} else {
						start = this.lastChildBeforeSorting.sourceEnd + 1;
					}
					if (i < length - 1) {
						end = this.children[i + 1].sourceStart - 1;
					} else {
						end = this.sourceEnd;
					}
				}
				buffer.append(SortElementBuilder.this.source, start, end - start + 1);
			} else {
				buffer.append(SortElementBuilder.this.source, this.nameSourceStart, this.sourceEnd - this.nameSourceStart + 1);
			}	
		}		
	}
	
	class SortMultipleFielDeclaration extends SortElement {
		int declarationStart;
		
		SortMultipleFielDeclaration(SortFieldDeclaration fieldDeclaration) {
			super(fieldDeclaration.declarationStart, fieldDeclaration.modifiers);
			this.declarationStart = fieldDeclaration.declarationStart;
			this.id = MULTIPLE_FIELD;
			this.innerFields = new SortFieldDeclaration[1];
			this.fieldCounter = 0;
			this.innerFields[this.fieldCounter++] = fieldDeclaration;
			this.numberOfPreceedingBlankLines = fieldDeclaration.numberOfPreceedingBlankLines;
			this.type = fieldDeclaration.type;
		}
		
		void addField(SortFieldDeclaration fieldDeclaration) {
			System.arraycopy(this.innerFields, 0, this.innerFields = new SortFieldDeclaration[this.fieldCounter + 1], 0, this.fieldCounter);
			this.innerFields[this.fieldCounter++] = fieldDeclaration;
		}
		
		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("multiple fields ") //$NON-NLS-1$
				.append(LINE_SEPARATOR);
			if (this.innerFields != null) {
				buffer
					.append(tab(tab + 1))
					.append("INNER FIELDS ------------------------------" + LINE_SEPARATOR); //$NON-NLS-1$
				for (int i = 0; i < this.fieldCounter; i++) {
					buffer.append(this.innerFields[i].toString(tab + 2));
					buffer.append(LINE_SEPARATOR);
				}
			}
		}

		ASTNode convert() {
			VariableDeclarationFragment variableDeclarationFragment = ast.newVariableDeclarationFragment();
			variableDeclarationFragment.setName(ast.newSimpleName(new String(this.innerFields[0].name)));
			variableDeclarationFragment.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.innerFields[0].sourceStart));
			FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(variableDeclarationFragment);

			for (int j = 1, max2 = this.innerFields.length; j < max2; j++) {
				VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
				fragment.setName(ast.newSimpleName(new String(this.innerFields[j].name)));
				fragment.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.innerFields[j].sourceStart));
			}
			String currentFieldType = this.type;
			
			int indexOfArrayBrace;
			if (currentFieldType.indexOf('.') != -1) {
				String[] typeParts = splitOn('.', currentFieldType);
				int length = typeParts.length;
				indexOfArrayBrace = typeParts[length - 1].indexOf('[');
				if (indexOfArrayBrace != -1) {
					int dimensions = occurencesOf('[', typeParts[length - 1]);
					typeParts[length - 1] = typeParts[length - 1].substring(0, indexOfArrayBrace);
					fieldDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast.newName(typeParts)), dimensions));
				} else {
					fieldDeclaration.setType(ast.newSimpleType(ast.newName(typeParts)));
				}
			} else if ((indexOfArrayBrace = currentFieldType.indexOf('[')) != -1) {
				int dimensions = occurencesOf('[', currentFieldType);
				currentFieldType = currentFieldType.substring(0, indexOfArrayBrace);
				fieldDeclaration.setType(ast.newArrayType(newType(currentFieldType), dimensions));
			} else {
				fieldDeclaration.setType(newType(currentFieldType));
			}
			fieldDeclaration.setModifiers(this.modifiers);
			return fieldDeclaration;
		}
		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			if (this.numberOfPreceedingBlankLines != 0) {
				dumpLineBreaks(this.numberOfPreceedingBlankLines, buffer, lineSeparator);
			}
			int length = this.fieldCounter;
			int start = this.innerFields[0].sourceStart;
			int end = this.innerFields[0].nameSourceStart - 1;
			buffer.append(SortElementBuilder.this.source, start, end - start + 1);
			
			for (int i = 0; i < length; i++) {
				this.innerFields[i].generateReduceSource(buffer, lineSeparator);
				if (i < length - 1) {
					start = this.innerFields[i].sourceEnd + 1;
					end = this.innerFields[i + 1].nameSourceStart - 1;
					buffer.append(SortElementBuilder.this.source, start, end - start + 1);
				}
			}
			start = this.innerFields[length - 1].sourceEnd + 1;
			end = this.innerFields[length - 1].declarationSourceEnd;
			buffer.append(SortElementBuilder.this.source, start, end - start + 1);
		}

		protected void sort() {
			for (int i = 0, max = this.fieldCounter; i < max; i++) {
				this.innerFields[i].sort();
			}
		}
	}

	class SortInitializer extends SortElement {
		SortInitializer(int sourceStart, int modifiers) {
			super(sourceStart, modifiers);
			this.id = INITIALIZER;
		}

		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("initializer " + LINE_SEPARATOR); //$NON-NLS-1$
		}
		
		ASTNode convert() {
			Initializer initializer = ast.newInitializer();
			initializer.setModifiers(this.modifiers);
			initializer.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));
			return initializer;
		}
		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			if (this.numberOfPreceedingBlankLines != 0) {
				dumpLineBreaks(this.numberOfPreceedingBlankLines, buffer, lineSeparator);
			}
			int length = this.children_count;
			if (length != 0) {
				int start = this.sourceStart;
				int end = this.firstChildBeforeSorting.sourceStart - 1;

				for (int i = 0; i < length; i++) {
					buffer.append(SortElementBuilder.this.source, start, end - start + 1);
					this.children[i].generateSource(buffer, lineSeparator);
					if (i < length - 1) {
						start = this.children[i].sourceEnd + 1;
					} else {
						start = this.lastChildBeforeSorting.sourceEnd + 1;
					}
					if (i < length - 1) {
						end = this.children[i + 1].sourceStart - 1;
					} else {
						end = this.sourceEnd;
					}
				}
				buffer.append(SortElementBuilder.this.source, start, end - start + 1);
			} else {
				buffer.append(SortElementBuilder.this.source, this.sourceStart, this.sourceEnd - this.sourceStart + 1);
			}	
		}

	}

	class SortClassDeclaration extends SortType  {
		SortClassDeclaration(int sourceStart, int modifiers, char[] name, char[] superclass, char[][] superinterfaces) {
			super(sourceStart, modifiers, name, superinterfaces);
			this.id = CLASS | TYPE;
			if (superclass != null) {
				this.superclass = new String(superclass);
			}
		}

		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("class ") //$NON-NLS-1$
				.append(this.name);
			if (this.superclass != null) {
				buffer.append(" extends " + this.superclass); //$NON-NLS-1$
			}
			if (this.superInterfaces != null) {
				int length = this.superInterfaces.length;
				buffer.append(" implements "); //$NON-NLS-1$
				for (int i = 0; i < length - 1; i++) {
					buffer.append(this.superInterfaces[i] + ", "); //$NON-NLS-1$
				}
				buffer.append(this.superInterfaces[length - 1]);
			}
			buffer.append(LINE_SEPARATOR);
		}
		
		ASTNode convert() {
			TypeDeclaration typeDeclaration = ast.newTypeDeclaration();
			typeDeclaration.setInterface(false);
			typeDeclaration.setModifiers(this.modifiers);
			typeDeclaration.setName(ast.newSimpleName(this.name));
			// set superclass
			if (this.superclass != null) {
				if (this.superclass.indexOf('.') == -1) {
					// the superclass is a simple name
					typeDeclaration.setSuperclass(ast.newSimpleName(this.superclass));
				} else {
					// the superclass is a qualified name
					String[] superclassNames = splitOn('.', this.superclass);
					typeDeclaration.setSuperclass(ast.newName(superclassNames));
				}
			}
			// set superinterfaces
			if (this.superInterfaces != null) {
				for (int j = 0, max2 = this.superInterfaces.length; j < max2; j++) {
					String currentInterfaceName = this.superInterfaces[j];
					Name interfaceName;
					if (currentInterfaceName.indexOf('.') == -1) {
						// the superclass is a simple name
						interfaceName = ast.newSimpleName(currentInterfaceName);
					} else {
						// the superclass is a qualified name
						String[] interfaceNames = splitOn('.', currentInterfaceName);
						interfaceName = ast.newName(interfaceNames);
					}
					typeDeclaration.superInterfaces().add(interfaceName);
				}
			}
			typeDeclaration.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));				
			return typeDeclaration;
		}			
	}

	abstract class SortType extends SortElement {
		SortType(int sourceStart, int modifier, char[] name, char[][] superinterfaces) {
			super(sourceStart, modifier);
			this.name = new String(name);
			if (superinterfaces != null) {
				int length = superinterfaces.length;
				this.superInterfaces = new String[length];
				for (int i = 0; i < length; i++) {
					this.superInterfaces[i] = new String(superinterfaces[i]);
				}
			}
		}
		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			if (this.numberOfPreceedingBlankLines != 0) {
				dumpLineBreaks(this.numberOfPreceedingBlankLines, buffer, lineSeparator);
			}
			int length = this.children_count;
			if (length != 0) {
				int start = this.sourceStart;
				int end = this.firstChildBeforeSorting.sourceStart;
				
				buffer.append(SortElementBuilder.this.source, start, end - start);
				
				for (int i = 0; i < length; i++) {
					((SortElementBuilder.SortElement)this.astNodes[i].getProperty(CORRESPONDING_ELEMENT)).generateSource(buffer, lineSeparator);
				}
				start = this.lastChildBeforeSorting.sourceEnd + 1;
				buffer.append(SortElementBuilder.this.source, start, this.sourceEnd - start + 1);
			} else {
				buffer.append(SortElementBuilder.this.source, this.sourceStart, this.sourceEnd - this.sourceStart + 1);
			}
		}
	}
	
	class SortInterfaceDeclaration extends SortType {
		SortInterfaceDeclaration(int sourceStart, int modifiers, char[] name, char[][] superinterfaces) {
			super(sourceStart, modifiers, name, superinterfaces);
			this.id = TYPE | INTERFACE;
		}
		void display(StringBuffer buffer, int tab) {
			buffer
				.append(tab(tab))
				.append("interface ") //$NON-NLS-1$
				.append(this.name);
			if (this.superInterfaces != null) {
				int length = this.superInterfaces.length;
				buffer.append(" implements "); //$NON-NLS-1$
				for (int i = 0; i < length - 1; i++) {
					buffer.append(this.superInterfaces[i] + ", "); //$NON-NLS-1$
				}
				buffer.append(this.superInterfaces[length - 1]);
			}
			buffer.append(LINE_SEPARATOR);
		}
		ASTNode convert() {
			TypeDeclaration typeDeclaration = ast.newTypeDeclaration();
			typeDeclaration.setInterface(true);
			typeDeclaration.setModifiers(this.modifiers);
			typeDeclaration.setName(ast.newSimpleName(this.name));
			// set superinterfaces
			if (this.superInterfaces != null) {
				for (int j = 0, max2 = this.superInterfaces.length; j < max2; j++) {
					String currentInterfaceName = this.superInterfaces[j];
					Name interfaceName;
					if (currentInterfaceName.indexOf('.') == -1) {
						// the superclass is a simple name
						interfaceName = ast.newSimpleName(currentInterfaceName);
					} else {
						// the superclass is a qualified name
						String[] interfaceNames = splitOn('.', currentInterfaceName);
						interfaceName = ast.newName(interfaceNames);
					}
					typeDeclaration.superInterfaces().add(interfaceName);
				}
			}
			typeDeclaration.setProperty(JavaCompilationUnitSorter.SOURCE_START, new Integer(this.sourceStart));				
			return typeDeclaration;
		}			
	}
	
	class SortCompilationUnit extends SortElement {
		SortCompilationUnit(int sourceStart) {
			super(sourceStart, 0);
			this.id = COMPILATION_UNIT;
		}
		void display(StringBuffer buffer, int tab) {
		}
		
		ASTNode convert() {
			return ast.newCompilationUnit();
		}
		/**
		 * @see org.eclipse.jdt.internal.core.SortElementBuilder.SortElement#generateSource(java.lang.StringBuffer)
		 */
		void generateSource(StringBuffer buffer, String lineSeparator) {
			int length = this.children_count;
			if (length != 0) {
				int end = this.firstChildBeforeSorting.sourceStart;
				buffer.append(SortElementBuilder.this.source, 0, end);
				for (int i = 0; i < length; i++) {
					((SortElementBuilder.SortElement)this.astNodes[i].getProperty(CORRESPONDING_ELEMENT)).generateSource(buffer, lineSeparator);
				}
				int start = this.lastChildBeforeSorting.sourceEnd + 1;
				buffer.append(SortElementBuilder.this.source, start, this.sourceEnd - start + 1);
			}
		}
	}

	SortElement currentElement;
	Stack stack;
	SortCompilationUnit compilationUnit;
	Scanner scanner;
	AST ast;

	char[] source;
	int[] lineEnds;
	int lastLineNumber;
	Comparator comparator;
	
	public SortElementBuilder(char[] source, Comparator comparator) {
		this.source = source;
		this.comparator = comparator;
		this.scanner = new Scanner(false, false, false, false, false, null, null);
		this.ast = new AST();
	}
	
	/*
	 * @see ISourceElementRequestor#acceptLineSeparatorPositions(int[])
	 */
	public void acceptLineSeparatorPositions(int[] positions) {
		this.lineEnds = positions;
	}
		
	public String getSource() {
		StringBuffer buffer = new StringBuffer();
		String lineSeparator = Util.findLineSeparator(this.source);
		if (lineSeparator == null) {
			lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$
		}
		this.compilationUnit.generateSource(buffer, lineSeparator);
		return buffer.toString();
	}

	private static int searchLineNumber(
		int[] startLineIndexes,
		int position) {
		// this code is completely useless, but it is the same implementation than
		// org.eclipse.jdt.internal.compiler.problem.ProblemHandler.searchLineNumber(int[], int)
		// if (startLineIndexes == null)
		//	return 1;
		int length = startLineIndexes.length;
		if (length == 0)
			return 1;
		int g = 0, d = length - 1;
		int m = 0;
		while (g <= d) {
			m = (g + d) / 2;
			if (position < startLineIndexes[m]) {
				d = m - 1;
			} else
				if (position > startLineIndexes[m]) {
					g = m + 1;
				} else {
					return m + 1;
				}
		}
		if (position < startLineIndexes[m]) {
			return m + 1;
		}
		return m + 2;
	}
	
	void sort() {
		compilationUnit.sort();
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterClass(int, int, char, int, int, char, char)
	 */
	public void enterClass(
		int declarationStart,
		int modifiers,
		char[] name,
		int nameSourceStart,
		int nameSourceEnd,
		char[] superclass,
		char[][] superinterfaces) {
			SortType type = new SortClassDeclaration(declarationStart, modifiers, name, superclass, superinterfaces);
			if ((this.currentElement.id & SortJavaElement.TYPE) != 0 && this.currentElement.children_count != 0) {
				recordLineNumberDifference(type);
			} else {
				recordLastLineNumber(declarationStart);
			}
			this.currentElement.addChild(type);
			push(type);
	}

	void recordLineNumberDifference(SortElement sortElement) {
		int lineNumber = searchLineNumber(this.lineEnds, sortElement.sourceStart);
		int diff = lineNumber - this.lastLineNumber - 1;
		if (diff != 0) {
			sortElement.numberOfPreceedingBlankLines = diff;
		}
		this.lastLineNumber = lineNumber;
	}

	void recordLastLineNumber(int sourceStart) {
		int lineNumber = searchLineNumber(this.lineEnds, sourceStart);
		this.lastLineNumber = lineNumber;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterCompilationUnit()
	 */
	public void enterCompilationUnit() {
		this.stack = new Stack();
		this.lastLineNumber = 0;
		push(compilationUnit = new SortCompilationUnit(0));
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterConstructor(int, int, char, int, int, char, char, char)
	 */
	public void enterConstructor(
		int declarationStart,
		int modifiers,
		char[] name,
		int nameSourceStart,
		int nameSourceEnd,
		char[][] parameterTypes,
		char[][] parameterNames,
		char[][] exceptionTypes) {
		if ((this.currentElement.id & SortJavaElement.TYPE) != 0) {
			SortConstructorDeclaration constructorDeclaration = new SortConstructorDeclaration(declarationStart, modifiers, parameterNames, parameterTypes, exceptionTypes);
			recordLineNumberDifference(constructorDeclaration);
			this.currentElement.addChild(constructorDeclaration);
			push(constructorDeclaration);
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterField(int, int, char, char, int, int)
	 */
	public void enterField(
		int declarationStart,
		int modifiers,
		char[] type,
		char[] name,
		int nameSourceStart,
		int nameSourceEnd) {
			if ((this.currentElement.id & SortJavaElement.TYPE) != 0) {
				SortFieldDeclaration fieldDeclaration = new SortFieldDeclaration(declarationStart, modifiers, type, name, nameSourceStart);
				SortElement[] currentElementChildren = this.currentElement.children;
				if (currentElementChildren != null) {
					SortElement previousElement = this.currentElement.children[this.currentElement.children_count - 1];
					if (previousElement.id == SortJavaElement.FIELD && ((SortFieldDeclaration) previousElement).declarationStart == declarationStart) {
						SortMultipleFielDeclaration multipleFielDeclaration = new SortMultipleFielDeclaration((SortFieldDeclaration) previousElement);
						multipleFielDeclaration.addField(fieldDeclaration);
						this.currentElement.children[this.currentElement.children_count - 1] = multipleFielDeclaration;
					} else if (previousElement.id == SortJavaElement.MULTIPLE_FIELD && ((SortMultipleFielDeclaration) previousElement).declarationStart == declarationStart) {
						((SortMultipleFielDeclaration) previousElement).addField(fieldDeclaration);
					} else {
						this.currentElement.addChild(fieldDeclaration);
						recordLineNumberDifference(fieldDeclaration);
					}
				} else {
					this.currentElement.addChild(fieldDeclaration);
					recordLineNumberDifference(fieldDeclaration);
				}
				push(fieldDeclaration);
			}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterInitializer(int, int)
	 */
	public void enterInitializer(int declarationStart, int modifiers) {
		if ((this.currentElement.id & SortJavaElement.TYPE) != 0) {
			SortInitializer initializer = new SortInitializer(declarationStart, modifiers);
			recordLineNumberDifference(initializer);
			this.currentElement.addChild(initializer);
			push(initializer);
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterInterface(int, int, char, int, int, char)
	 */
	public void enterInterface(
		int declarationStart,
		int modifiers,
		char[] name,
		int nameSourceStart,
		int nameSourceEnd,
		char[][] superinterfaces) {
			SortType type = new SortInterfaceDeclaration(declarationStart, modifiers, name, superinterfaces);
			if ((this.currentElement.id & SortJavaElement.TYPE) != 0 && this.currentElement.children_count != 0) {
				recordLineNumberDifference(type);
			} else {
				recordLastLineNumber(declarationStart);
			}
			this.currentElement.addChild(type);
			push(type);
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#enterMethod(int, int, char, char, int, int, char, char, char)
	 */
	public void enterMethod(
		int declarationStart,
		int modifiers,
		char[] returnType,
		char[] name,
		int nameSourceStart,
		int nameSourceEnd,
		char[][] parameterTypes,
		char[][] parameterNames,
		char[][] exceptionTypes) {
			if ((this.currentElement.id & SortJavaElement.TYPE) != 0) {
				SortMethodDeclaration methodDeclaration = new SortMethodDeclaration(declarationStart, modifiers, name, parameterNames, parameterTypes, exceptionTypes, returnType);
				recordLineNumberDifference(methodDeclaration);
				this.currentElement.addChild(methodDeclaration);
				push(methodDeclaration);
			}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitClass(int)
	 */
	public void exitClass(int declarationEnd) {
		pop(declarationEnd);
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitCompilationUnit(int)
	 */
	public void exitCompilationUnit(int declarationEnd) {
		pop(declarationEnd);
		sort();
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitConstructor(int)
	 */
	public void exitConstructor(int declarationEnd) {
		pop(declarationEnd);
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitField(int, int)
	 */
	public void exitField(int initializationStart, int declarationEnd, int declarationSourceEnd) {
		int normalizedDeclarationSourceEnd = this.normalizeSourceEnd(declarationSourceEnd);
		if (this.currentElement.id == SortJavaElement.FIELD) {
			((SortFieldDeclaration) this.currentElement).declarationSourceEnd = normalizedDeclarationSourceEnd;
		}
		pop(declarationEnd);
		SortElement element = this.currentElement.children[this.currentElement.children_count - 1];
		if (element.id == SortJavaElement.MULTIPLE_FIELD) {
			SortMultipleFielDeclaration multipleFielDeclaration = (SortMultipleFielDeclaration) element;
			multipleFielDeclaration.innerFields[multipleFielDeclaration.fieldCounter - 1].declarationSourceEnd = normalizedDeclarationSourceEnd;
			multipleFielDeclaration.sourceEnd = normalizedDeclarationSourceEnd;
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitInitializer(int)
	 */
	public void exitInitializer(int declarationEnd) {
		pop(declarationEnd);
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitInterface(int)
	 */
	public void exitInterface(int declarationEnd) {
		pop(declarationEnd);
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ISourceElementRequestor#exitMethod(int)
	 */
	public void exitMethod(int declarationEnd) {
		pop(declarationEnd);
	}

	final int normalizeSourceStart(int position) {
		int lineNumber = searchLineNumber(this.lineEnds, position);
		if (lineNumber == 1) {
			return 0;
		}
		int normalizeSourceStart = this.lineEnds[lineNumber - 2] + 1;
		int index = position;
		index--;
		while (Character.isWhitespace(this.source[index]) && index > normalizeSourceStart) {
			index--;
		}
		if (index == normalizeSourceStart) {
			return normalizeSourceStart;
		} else {
			return index + 1;
		}
	}

	final int normalizeSourceEnd(int position) {
		int lineNumber = searchLineNumber(this.lineEnds, position);
		if (lineNumber == 1) {
			return position;
		}
		int normalizeSourceEnd = 0;
		if (lineNumber - 1 >= this.lineEnds.length) {
			normalizeSourceEnd = this.source.length - 1;
		} else {
			normalizeSourceEnd = this.lineEnds[lineNumber - 1];
		}
		int index = position + 1;
		while (index < normalizeSourceEnd && Character.isWhitespace(this.source[index])) {
			index++;
		}
		if (index == normalizeSourceEnd) {
			return normalizeSourceEnd;
		} else {
			return position;
		}
	}
	
	private void pop(int declarationEnd) {
		this.currentElement.sourceEnd = normalizeSourceEnd(declarationEnd);
		this.lastLineNumber = searchLineNumber(this.lineEnds, declarationEnd);
		this.currentElement.closeCollections();
		this.stack.pop();
		if (!this.stack.isEmpty()) {
			this.currentElement = (SortElement) this.stack.peek();
		}
	}
	
	private void push(SortElement sortElement) {
		this.currentElement = sortElement;
		this.stack.push(sortElement);
	}
}