/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.core.dom;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.CompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.CancelableNameEnvironment;
import org.eclipse.jdt.internal.core.CancelableProblemFactory;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;

class CompilationUnitResolver extends Compiler {
	
	/**
	 * Answer a new CompilationUnitVisitor using the given name environment and compiler options.
	 * The environment and options will be in effect for the lifetime of the compiler.
	 * When the compiler is run, compilation results are sent to the given requestor.
	 *
	 *  @param environment org.eclipse.jdt.internal.compiler.api.env.INameEnvironment
	 *      Environment used by the compiler in order to resolve type and package
	 *      names. The name environment implements the actual connection of the compiler
	 *      to the outside world (for example, in batch mode the name environment is performing
	 *      pure file accesses, reuse previous build state or connection to repositories).
	 *      Note: the name environment is responsible for implementing the actual classpath
	 *            rules.
	 *
	 *  @param policy org.eclipse.jdt.internal.compiler.api.problem.IErrorHandlingPolicy
	 *      Configurable part for problem handling, allowing the compiler client to
	 *      specify the rules for handling problems (stop on first error or accumulate
	 *      them all) and at the same time perform some actions such as opening a dialog
	 *      in UI when compiling interactively.
	 *      @see org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies
	 * 
	 *	@param settings The settings to use for the resolution.
	 *      
	 *  @param requestor org.eclipse.jdt.internal.compiler.api.ICompilerRequestor
	 *      Component which will receive and persist all compilation results and is intended
	 *      to consume them as they are produced. Typically, in a batch compiler, it is 
	 *      responsible for writing out the actual .class files to the file system.
	 *      @see org.eclipse.jdt.internal.compiler.CompilationResult
	 *
	 *  @param problemFactory org.eclipse.jdt.internal.compiler.api.problem.IProblemFactory
	 *      Factory used inside the compiler to create problem descriptors. It allows the
	 *      compiler client to supply its own representation of compilation problems in
	 *      order to avoid object conversions. Note that the factory is not supposed
	 *      to accumulate the created problems, the compiler will gather them all and hand
	 *      them back as part of the compilation unit result.
	 */
	public CompilationUnitResolver(
		INameEnvironment environment,
		IErrorHandlingPolicy policy,
		Map settings,
		ICompilerRequestor requestor,
		IProblemFactory problemFactory) {

		super(environment, policy, settings, requestor, problemFactory, false);
	}
	
	/*
	 * Add additional source types
	 */
	public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction) {
		CompilationResult result =
			new CompilationResult(sourceTypes[0].getFileName(), 1, 1, this.options.maxProblemsPerUnit);
		// need to hold onto this
		CompilationUnitDeclaration unit =
			SourceTypeConverter.buildCompilationUnit(
				sourceTypes,//sourceTypes[0] is always toplevel here
				SourceTypeConverter.FIELD_AND_METHOD // need field and methods
				| SourceTypeConverter.MEMBER_TYPE // need member types
				| SourceTypeConverter.FIELD_INITIALIZATION, // need field initialization: see bug 40476
				this.lookupEnvironment.problemReporter,
				result);

		if (unit != null) {
			this.lookupEnvironment.buildTypeBindings(unit, accessRestriction);
			this.lookupEnvironment.completeTypeBindings(unit, true);
		}
	}

	public static ASTNode convert(CompilationUnitDeclaration compilationUnitDeclaration, char[] source, int apiLevel, Map options, boolean needToResolveBindings, WorkingCopyOwner owner, DefaultBindingResolver.BindingTables bindingTables, IProgressMonitor monitor) {
		BindingResolver resolver = null;
		AST ast = AST.newAST(apiLevel);
		ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
		CompilationUnit compilationUnit = null;
		ASTConverter converter = new ASTConverter(options, needToResolveBindings, monitor);
		if (needToResolveBindings) {
			resolver = new DefaultBindingResolver(compilationUnitDeclaration.scope, owner, bindingTables);
		} else {
			resolver = new BindingResolver();
		}
		ast.setBindingResolver(resolver);
		converter.setAST(ast);
		compilationUnit = converter.convert(compilationUnitDeclaration, source);
		compilationUnit.setLineEndTable(compilationUnitDeclaration.compilationResult.lineSeparatorPositions);
		ast.setDefaultNodeFlag(0);
		ast.setOriginalModificationCount(ast.modificationCount());
		return compilationUnit;
	}
	
	/*
	 *  Low-level API performing the actual compilation
	 */
	protected static IErrorHandlingPolicy getHandlingPolicy() {

		// passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)	
		return new IErrorHandlingPolicy() {
			public boolean stopOnFirstError() {
				return false;
			}
			public boolean proceedOnErrors() {
				return false; // stop if there are some errors 
			}
		};
	}

	/*
	 * Answer the component to which will be handed back compilation results from the compiler
	 */
	protected static ICompilerRequestor getRequestor() {
		return new ICompilerRequestor() {
			public void acceptResult(CompilationResult compilationResult) {
				// do nothing
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.Compiler#initializeParser()
	 */
	public void initializeParser() {
		this.parser = new CommentRecorderParser(this.problemReporter, false);
	}
	/*
	 * Compiler crash recovery in case of unexpected runtime exceptions
	 */
	protected void handleInternalException(
			Throwable internalException,
			CompilationUnitDeclaration unit,
			CompilationResult result) {
		super.handleInternalException(internalException, unit, result);
		if (unit != null) {
			removeUnresolvedBindings(unit);
		}
	}
	
	/*
	 * Compiler recovery in case of internal AbortCompilation event
	 */
	protected void handleInternalException(
			AbortCompilation abortException,
			CompilationUnitDeclaration unit) {
		super.handleInternalException(abortException, unit);
		if (unit != null) {
			removeUnresolvedBindings(unit);
		}
	}	
	
	public static void parse(ASTRequestor astRequestor, int apiLevel, Map options, IProgressMonitor monitor) {
		ICompilationUnit[] workingCopies = astRequestor.getSources();
		CompilerOptions compilerOptions = new CompilerOptions(options);
		Parser parser = new CommentRecorderParser(
			new ProblemReporter(
					DefaultErrorHandlingPolicies.proceedWithAllProblems(), 
					compilerOptions, 
					new DefaultProblemFactory()),
			false);
		while (workingCopies != null) {
			for (int i = 0, length = workingCopies.length; i < length; i++) {
				org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit = (org.eclipse.jdt.internal.compiler.env.ICompilationUnit) workingCopies[i];
				CompilationResult compilationResult = new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
				CompilationUnitDeclaration compilationUnitDeclaration = parser.dietParse(sourceUnit, compilationResult);
				
				if (compilationUnitDeclaration.ignoreMethodBodies) {
					compilationUnitDeclaration.ignoreFurtherInvestigation = true;
					// if initial diet parse did not work, no need to dig into method bodies.
					continue; 
				}
				
				//fill the methods bodies in order for the code to be generated
				//real parse of the method....
				parser.scanner.setSource(compilationResult);
				org.eclipse.jdt.internal.compiler.ast.TypeDeclaration[] types = compilationUnitDeclaration.types;
				if (types != null) {
					for (int j = types.length; --j >= 0;)
						types[j].parseMethod(parser, compilationUnitDeclaration);
				}
				
				// convert AST
				ASTNode node = convert(compilationUnitDeclaration, sourceUnit.getContents(), apiLevel, options, false/*don't resolve binding*/, null/*no owner needed*/, null/*no binding table needed*/, monitor);
				
				
				// accept AST
				astRequestor.acceptAST(node);
			}
			
			workingCopies = astRequestor.getSources();
		}
	}
	
	public static CompilationUnitDeclaration parse(org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit, NodeSearcher nodeSearcher, Map settings) {
		if (sourceUnit == null) {
			throw new IllegalArgumentException();
		}
		CompilerOptions compilerOptions = new CompilerOptions(settings);
		Parser parser = new CommentRecorderParser(
			new ProblemReporter(
					DefaultErrorHandlingPolicies.proceedWithAllProblems(), 
					compilerOptions, 
					new DefaultProblemFactory()),
			false);
		CompilationResult compilationResult = new CompilationResult(sourceUnit, 0, 0, compilerOptions.maxProblemsPerUnit);
		CompilationUnitDeclaration compilationUnitDeclaration = parser.dietParse(sourceUnit, compilationResult);
		
		if (compilationUnitDeclaration.ignoreMethodBodies) {
			compilationUnitDeclaration.ignoreFurtherInvestigation = true;
			// if initial diet parse did not work, no need to dig into method bodies.
			return null; 
		}
		
		if (nodeSearcher != null) {
			char[] source = sourceUnit.getContents();
			int searchPosition = nodeSearcher.position;
			if (searchPosition < 0 || searchPosition > source.length) {
				// the position is out of range. There is no need to search for a node.
	 			return compilationUnitDeclaration;
			}
		
			compilationUnitDeclaration.traverse(nodeSearcher, compilationUnitDeclaration.scope);
			
			org.eclipse.jdt.internal.compiler.ast.ASTNode node = nodeSearcher.found;
	 		if (node == null) {
	 			return compilationUnitDeclaration;
	 		}
	 		
	 		org.eclipse.jdt.internal.compiler.ast.TypeDeclaration enclosingTypeDeclaration = nodeSearcher.enclosingType;
	 		
			if (node instanceof AbstractMethodDeclaration) {
				((AbstractMethodDeclaration)node).parseStatements(parser, compilationUnitDeclaration);
			} else if (enclosingTypeDeclaration != null) {
				if (node instanceof org.eclipse.jdt.internal.compiler.ast.Initializer) {
					((org.eclipse.jdt.internal.compiler.ast.Initializer) node).parseStatements(parser, enclosingTypeDeclaration, compilationUnitDeclaration);
				} else {  					
					((org.eclipse.jdt.internal.compiler.ast.TypeDeclaration)node).parseMethod(parser, compilationUnitDeclaration);
				} 				
			}
		} else {
			//fill the methods bodies in order for the code to be generated
			//real parse of the method....
			parser.scanner.setSource(compilationResult);
			org.eclipse.jdt.internal.compiler.ast.TypeDeclaration[] types = compilationUnitDeclaration.types;
			if (types != null) {
				for (int i = types.length; --i >= 0;)
					types[i].parseMethod(parser, compilationUnitDeclaration);
			}
		}
		return compilationUnitDeclaration;
	}

	public static void resolve(
		ASTRequestor requestor,
		int apiLevel,
		Map options,
		IJavaProject javaProject,
		WorkingCopyOwner owner,
		IProgressMonitor monitor) {
	
		CancelableNameEnvironment environment = null;
		CancelableProblemFactory problemFactory = null;
		try {
			environment = new CancelableNameEnvironment(((JavaProject) javaProject), owner, monitor);
			problemFactory = new CancelableProblemFactory(monitor);
			CompilationUnitResolver resolver =
				new CompilationUnitResolver(
					environment,
					getHandlingPolicy(),
					options,
					getRequestor(),
					problemFactory);

			resolver.resolve(requestor, apiLevel, options, owner, monitor);
		} catch (JavaModelException e) {
			// project doesn't exist -> simple parse without resolving
			parse(requestor, apiLevel, options, monitor);
		} finally {
			if (environment != null) {
				environment.monitor = null; // don't hold a reference to this external object
			}
			if (problemFactory != null) {
				problemFactory.monitor = null; // don't hold a reference to this external object
			}
		}
	}
	public static CompilationUnitDeclaration resolve(
		org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
		IJavaProject javaProject,
		NodeSearcher nodeSearcher,
		Map options,
		WorkingCopyOwner owner,
		IProgressMonitor monitor)
		throws JavaModelException {
	
		CompilationUnitDeclaration unit = null;
		CancelableNameEnvironment environment = null;
		CancelableProblemFactory problemFactory = null;
		try {
			environment = new CancelableNameEnvironment(((JavaProject)javaProject), owner, monitor);
			problemFactory = new CancelableProblemFactory(monitor);
			CompilationUnitResolver resolver =
				new CompilationUnitResolver(
					environment,
					getHandlingPolicy(),
					options,
					getRequestor(),
					problemFactory);

			unit = 
				resolver.resolve(
					null, // no existing compilation unit declaration
					sourceUnit,
					nodeSearcher,
					true, // method verification
					true, // analyze code
					true); // generate code					
			return unit;
		} finally {
			if (environment != null) {
				environment.monitor = null; // don't hold a reference to this external object
			}
			if (problemFactory != null) {
				problemFactory.monitor = null; // don't hold a reference to this external object
			}
			// unit cleanup is done by caller
		}
	}
	/*
	 * When unit result is about to be accepted, removed back pointers
	 * to unresolved bindings
	 */
	public void removeUnresolvedBindings(CompilationUnitDeclaration compilationUnitDeclaration) {
		final org.eclipse.jdt.internal.compiler.ast.TypeDeclaration[] types = compilationUnitDeclaration.types;
		if (types != null) {
			for (int i = 0, max = types.length; i < max; i++) {
				removeUnresolvedBindings(types[i]);
			}
		}
	}
	private void removeUnresolvedBindings(org.eclipse.jdt.internal.compiler.ast.TypeDeclaration type) {
		final org.eclipse.jdt.internal.compiler.ast.TypeDeclaration[] memberTypes = type.memberTypes;
		if (memberTypes != null) {
			for (int i = 0, max = memberTypes.length; i < max; i++){
				removeUnresolvedBindings(memberTypes[i]);
			}
		}
		if (type.binding != null && (type.binding.modifiers & CompilerModifiers.AccUnresolved) != 0) {
			type.binding = null;
		}
		
		final org.eclipse.jdt.internal.compiler.ast.FieldDeclaration[] fields = type.fields;
		if (fields != null) {
			for (int i = 0, max = fields.length; i < max; i++){
				if (fields[i].binding != null && (fields[i].binding.modifiers & CompilerModifiers.AccUnresolved) != 0) {
					fields[i].binding = null;
				}
			}
		}
	
		final AbstractMethodDeclaration[] methods = type.methods;
		if (methods != null) {
			for (int i = 0, max = methods.length; i < max; i++){
				if (methods[i].binding !=  null && (methods[i].binding.modifiers & CompilerModifiers.AccUnresolved) != 0) {
					methods[i].binding = null;
				}
			}
		}
	}

	private void resolve(ASTRequestor astRequestor, int apiLevel, Map compilerOptions, WorkingCopyOwner owner, IProgressMonitor monitor) {

		DefaultBindingResolver.BindingTables bindingTables = new DefaultBindingResolver.BindingTables();
		CompilationUnitDeclaration unit = null;
		int i = 0;
		try {
			ICompilationUnit[] workingCopies = astRequestor.getSources();
			if (workingCopies == null) return;
			int length = workingCopies.length;
			org.eclipse.jdt.internal.compiler.env.ICompilationUnit[] sourceUnits = new org.eclipse.jdt.internal.compiler.env.ICompilationUnit[length];
			System.arraycopy(workingCopies, 0, sourceUnits, 0, length);
			beginToCompile(sourceUnits);
			// process all units (some more could be injected in the loop by the lookup environment)
			for (; i < this.totalUnits; i++) {
				unit = this.unitsToProcess[i];
				try {
					process(unit, i);
					
					// convert AST
					CompilationResult compilationResult = unit.compilationResult;
					org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit = compilationResult.compilationUnit;
					char[] contents = sourceUnit.getContents();
					AST ast = AST.newAST(apiLevel);
					ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
					ASTConverter converter = new ASTConverter(compilerOptions, true/*need to resolve bindings*/, monitor);
					BindingResolver resolver = new DefaultBindingResolver(unit.scope, owner, bindingTables);
					ast.setBindingResolver(resolver);
					converter.setAST(ast);
					CompilationUnit compilationUnit = converter.convert(unit, contents);
					compilationUnit.setLineEndTable(compilationResult.lineSeparatorPositions);
					ast.setDefaultNodeFlag(0);
					ast.setOriginalModificationCount(ast.modificationCount());
					
					// pass it to requestor
					astRequestor.acceptAST(compilationUnit);
				} finally {
					// cleanup compilation unit result
					unit.cleanUp();
				}
				this.unitsToProcess[i] = null; // release reference to processed unit declaration
				this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
				
				if (i == this.totalUnits-1) {
					// end of batch: look for new one
					do {
						workingCopies = astRequestor.getSources();
					} while (workingCopies != null && workingCopies.length == 0);
					if (workingCopies != null) {
						for (int j = 0, workingCopyLength = workingCopies.length; j < workingCopyLength; j++) {
							ICompilationUnit workingCopy = workingCopies[j];
							accept((org.eclipse.jdt.internal.compiler.env.ICompilationUnit) workingCopy, null /* no access restriction*/);
						}
					}
				}
			}
		} catch (AbortCompilation e) {
			this.handleInternalException(e, unit);
		} catch (Error e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} catch (RuntimeException e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} finally {
			// No reset is performed there anymore since,
			// within the CodeAssist (or related tools),
			// the compiler may be called *after* a call
			// to this resolve(...) method. And such a call
			// needs to have a compiler with a non-empty
			// environment.
			// this.reset();
		}
	}
	
	private CompilationUnitDeclaration resolve(
			CompilationUnitDeclaration unit,
			org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
			NodeSearcher nodeSearcher,
			boolean verifyMethods,
			boolean analyzeCode,
			boolean generateCode) {

		try {

			if (unit == null) {
				// build and record parsed units
				this.parseThreshold = 0; // will request a full parse
				beginToCompile(new org.eclipse.jdt.internal.compiler.env.ICompilationUnit[] { sourceUnit });
				// process all units (some more could be injected in the loop by the lookup environment)
				unit = this.unitsToProcess[0];
			} else {
				// initial type binding creation
				this.lookupEnvironment.buildTypeBindings(unit, null /*no access restriction*/);

				// binding resolution
				this.lookupEnvironment.completeTypeBindings();
			}

			if (nodeSearcher == null) {
				this.parser.getMethodBodies(unit); // no-op if method bodies have already been parsed
			} else {
				int searchPosition = nodeSearcher.position;
				if (searchPosition >= 0 && searchPosition <= sourceUnit.getContents().length) {
					unit.traverse(nodeSearcher, unit.scope);
					
					org.eclipse.jdt.internal.compiler.ast.ASTNode node = nodeSearcher.found;
					
		 			if (node != null) {
						org.eclipse.jdt.internal.compiler.ast.TypeDeclaration enclosingTypeDeclaration = nodeSearcher.enclosingType;
		  				if (node instanceof AbstractMethodDeclaration) {
							((AbstractMethodDeclaration)node).parseStatements(this.parser, unit);
		 				} else if (enclosingTypeDeclaration != null) {
							if (node instanceof org.eclipse.jdt.internal.compiler.ast.Initializer) {
			 					((org.eclipse.jdt.internal.compiler.ast.Initializer) node).parseStatements(this.parser, enclosingTypeDeclaration, unit);
		 					} else if (node instanceof org.eclipse.jdt.internal.compiler.ast.TypeDeclaration) {  					
								((org.eclipse.jdt.internal.compiler.ast.TypeDeclaration)node).parseMethod(this.parser, unit);
							} 				
		 				}
		 			}
				}
			}
			
			if (unit.scope != null) {
				// fault in fields & methods
				unit.scope.faultInTypes();
				if (unit.scope != null && verifyMethods) {
					// http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
 					// verify inherited methods
					unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
				}
				// type checking
				unit.resolve();		

				// flow analysis
				if (analyzeCode) unit.analyseCode();
		
				// code generation
				if (generateCode) unit.generateCode();
			}
			if (this.unitsToProcess != null) this.unitsToProcess[0] = null; // release reference to processed unit declaration
			this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
			return unit;
		} catch (AbortCompilation e) {
			this.handleInternalException(e, unit);
			return unit == null ? this.unitsToProcess[0] : unit;
		} catch (Error e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} catch (RuntimeException e) {
			this.handleInternalException(e, unit, null);
			throw e; // rethrow
		} finally {
			// No reset is performed there anymore since,
			// within the CodeAssist (or related tools),
			// the compiler may be called *after* a call
			// to this resolve(...) method. And such a call
			// needs to have a compiler with a non-empty
			// environment.
			// this.reset();
		}
	}
	/*
	 * Internal API used to resolve a given compilation unit. Can run a subset of the compilation process
	 */
	public CompilationUnitDeclaration resolve(
			org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit, 
			boolean verifyMethods,
			boolean analyzeCode,
			boolean generateCode) {
				
		return resolve(
			null, /* no existing compilation unit declaration*/
			sourceUnit,
			null/*no node searcher*/, 
			verifyMethods,
			analyzeCode,
			generateCode);
	}

	/*
	 * Internal API used to resolve a given compilation unit. Can run a subset of the compilation process
	 */
	public CompilationUnitDeclaration resolve(
			CompilationUnitDeclaration unit, 
			org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit, 
			boolean verifyMethods,
			boolean analyzeCode,
			boolean generateCode) {
		
		return resolve(
			unit, 
			sourceUnit, 
			null/*no node searcher*/, 
			verifyMethods, 
			analyzeCode, 
			generateCode);
	}
}