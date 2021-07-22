#!/bin/sh 

    # Create a Flora-2 tarball 

    # RUN this in the ./admin/ directory!

files="./flora2/INSTALLATION ./flora2/LICENSE \
        ./flora2/Makefile ./flora2/NMakefile.mak \
    	./flora2/makeflora ./flora2/runflora ./flora2/floraconfig \
    	./flora2/etc \
        ./flora2/*.bat  ./flora2/*.sh \
        ./flora2/*.P ./flora2/*.H ./flora2/*.xwam ./flora2/*.flh\
        ./flora2/AT/*.flr ./flora2/AT/Makefile ./flora2/AT/NMakefile.mak \
        ./flora2/AT/prolog/*.P  ./flora2/AT/include/*.flh  \
        ./flora2/ATinc/*.flh  \
    	./flora2/closure/Makefile ./flora2/closure/NMakefile.mak \
    	./flora2/closure/*.fli \
    	./flora2/closure/*.flh \
    	./flora2/closure/*.inc \
    	./flora2/datatypes/*.P ./flora2/datatypes/*.xwam \
        ./flora2/datatypes/*.sh \
    	./flora2/datatypes/Makefile ./flora2/datatypes/NMakefile.mak \
    	./flora2/debugger/*.in ./flora2/debugger/*.P ./flora2/debugger/*.xwam \
    	./flora2/debugger/*.dat \
    	./flora2/debugger/Makefile ./flora2/debugger/NMakefile.mak \
    	./flora2/demos/*.flr ./flora2/demos/*.sh \
    	./flora2/demos/sgml/*.flr ./flora2/demos/sgml/expectedoutput \
    	./flora2/demos/xpath/*.flr ./flora2/demos/xpath/expectedoutput \
    	./flora2/demos/Makefile ./flora2/demos/NMakefile.mak \
    	./flora2/docs/*.pdf \
    	./flora2/docs/NMakefile.mak \
    	./flora2/emacs/flora.el* \
    	./flora2/emacs/README \
    	./flora2/emacs/Makefile \
    	./flora2/emacs/NMakefile.mak \
    	./flora2/flrincludes/*.flh \
    	./flora2/genincludes/Makefile ./flora2/genincludes/NMakefile.mak \
    	./flora2/genincludes/*.fli \
    	./flora2/genincludes/*.flh \
    	./flora2/genincludes/*.inc \
    	./flora2/headerinc/*.flh \
    	./flora2/includes/*.flh \
    	./flora2/includes/*.fli \
    	./flora2/includes/Makefile ./flora2/includes/NMakefile.mak \
    	./flora2/lib/*.flr ./flora2/lib/Makefile ./flora2/lib/NMakefile.mak \
	./flora2/lib/include/*flh \
    	./flora2/libinc/*.flh \
    	./flora2/cc/prolog2hilog.* ./flora2/cc/*.P \
    	./flora2/cc/flora_ground.* \
    	./flora2/cc/flrcc_init.P \
        ./flora2/cc/Makefile \
	./flora2/cc/NMakefile64.mak \
        ./flora2/cc/windows64/*.dll ./flora2/cc/windows64/*.exp \
    	./flora2/cc/windows64/*.lib \
    	./flora2/cc/C_calling_Ergo/* \
    	./flora2/pkgs/Makefile ./flora2/pkgs/NMakefile.mak \
        ./flora2/pkgs/*.flr ./flora2/pkgs/prolog/*.P \
	./flora2/pkgs/include/*.flh \
        ./flora2/syslib/*.xwam \
    	./flora2/syslib/*.P ./flora2/syslib/*.H ./flora2/syslib/*.sh \
    	./flora2/syslib/Makefile ./flora2/syslib/NMakefile.mak \
    	./flora2/syslibinc/*.flh  \
        ./flora2/java "

curdir=`pwd`
curdir=`basename $curdir`
if test ! $curdir = admin; then
    echo "+++ This script must be run out of flora2/admin/ directory"
    exit 1
fi

cd ../..

EXCLUDEFILE=flora2/admin/.excludedFiles

cat > $EXCLUDEFILE <<EOF
CVS
*.conf
*.log
.#*
.cvsignore
.svn
.excludedFiles
*.zip
*.tar
*.bz2
*.gz
*.Z
*~
*.bak
*-sv
*-old
EOF

tar cvf flora2/flora2.tar --exclude-from=$EXCLUDEFILE $files --transform 's,^.,flora2release,'

gzip -f flora2/flora2.tar
