#!/bin/sh
# $Id: build-release.sh,v 1.8 2005/06/05 16:42:31 linus Exp $

# The purpose of this shellscript is to make all the release work.

CHILDPROJECTS="argouml-csharp argouml-nb argouml-i18n-zh"

# Check that JAVA_HOME is set.
if test ! -x $JAVA_HOME/bin/javac
then
    echo JAVA_HOME is not set correctly.
    exit 1;
fi

if test ! -n "$CVSROOT"
then
    echo CVSROOT is not set.
    exit 1;
fi

if test ! -d ../svn/argouml-downloads/trunk/www
then
    echo The output directory ../svn/argouml-downloads/trunk/www does not exist.
    exit 1;
fi

if test -f $HOME/argouml.build.properties
then
    echo $HOME/argouml.build.properties exists.
    exit
fi

BUILD=BUILDRELEASE:

# Set up things
echo "$BUILD You have to set up the cvs login correctly!"
# 1. Tag the whole CVS repository with the freeze tag!
echo "$BUILD You also need to tag the respostory with the freeze tag!"
# 2. Check out a new copy of the source!
echo $BUILD Will check out a new copy of the source.
echo "Give the tag name of the freeze tag (like VERSION_X_Y_Z_F):"
read freezetag
mkdir "$freezetag" || exit 1;
cd "$freezetag"
echo "Give the tag name of the release tag (like VERSION_X_Y_Z):"
releasetag=`echo $freezetag | sed 's/_F$//'`
echo "Calculated the release tag to $releasetag" 

echo $BUILD Checking out
cvs co -r "$freezetag" argouml/src_new
cvs co -r "$freezetag" argouml/src
cvs co -r "$freezetag" argouml/modules
cvs co -r "$freezetag" argouml/lib
cvs co -r "$freezetag" argouml/tools
cvs co -r "$freezetag" argouml/tests
cvs co -r "$freezetag" argouml/documentation
cvs co -r "$freezetag" $CHILDPROJECTS

# 3. Build the release.
echo "$BUILD Will build the release."
chmod +x argouml/tools/ant-1.4.1/bin/ant
( cd argouml/src_new && ./build.sh package )

# Build the childprojects and copy the result to build/ext:
for proj in $CHILDPROJECTS
do
    ( cd $proj && ../argouml/tools/ant-1.4.1/bin/ant install )
done


echo "$BUILD build the documentation in pdf."
( cd argouml/documentation && ../tools/ant-1.4.1/bin/ant docbook-xsl-get )
if test ! -d argouml/documentation/docbook-setup/docbook-xsl-1.66.1
then
    echo "docbook-xsl download failed. Fix it and press return."
    read garbage
fi

JIMI=argouml/tools/lib/JimiProClasses.zip
if test -f ../$JIMI
then
    cp ../$JIMI $JIMI
else
    echo "Copy the jimi file to argouml/tools/lib! and press return."
    read garbage
fi
( cd argouml/documentation && ../tools/ant-1.4.1/bin/ant pdf ) &

echo "$BUILD sign the files for Java Web Start"
echo "$BUILD (all files distributed will be signed)."
(
  cd argouml/build &&
  find . -name \*.jar -print |
  while read jarname
  do
    echo "$BUILD signing $jarname"
    $JAVA_HOME/bin/jarsigner -storepass secret $jarname argouml
  done
)


# 4. Test the release!
echo "$BUILD Will test the release."
( cd argouml/src_new && ./build.sh alltests

echo "$BUILD Starting ArgoUML for you to do the manual testing in modules/junit"
echo "$BUILD Give the test case TestAll, uncheck Reload at every run"
echo "$BUILD When done, Exit the tool."
( cd argouml/modules/junit && ../../tools/ant-1.4.1/bin/ant run )
echo "$BUILD Tests done."
echo "$BUILD No more input."

# 5. Tag with the release tag
for proj in argouml $CHILDPROJECTS
do
    ( cd $proj && cvs tag $releasetag )
done
wait

releasename=`sed '/^argo.core.version=/!d;s/argo.core.version=//' < argouml/documentation/default.properties`
for pdffile in argouml/build/documentation/pdf/*/*.pdf
do
  mv $pdffile argouml/build/`basename $pdffile .pdf`-${releasename}.pdf
done
