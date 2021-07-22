#!/bin/sh

FLRVERSION=./version.flh
ERGOVERSION=./ergoisms/version.flh
ERGOSWITCH=./ergoisms/ergo.switch

if [ -e $ERGOSWITCH ]; then
    version=`git rev-parse --short=7 HEAD`
    perl -i -pe "s/Revision: [^\n]*/Revision: $version'/"  $ERGOVERSION
    perl -i -pe "use POSIX qw(strftime); my \$date=strftime \"%F %T\", gmtime; s/Date: [^\n]*/Date: \$date'/"  $ERGOVERSION
else
    version=`git svn info 2> /dev/null | grep "Revision:"`
    if [ "$version" != "" ]; then
        # git svn works
        perl -i -pe "s/Revision: [^\n]*/Revision: $version'/"  $FLRVERSION
        perl -i -pe "use POSIX qw(strftime); my \$date=strftime \"%F %T\", gmtime; s/Date: [^\n]*/Date: \$date'/"  $FLRVERSION
    else
        # smartgit-svn integration
        for arg in `git rev-list HEAD`; do
            version=`git log -1 --pretty="%B" $arg | grep git-svn-id | sed -e 's/^.*@\([0-9]*\).*$/\1/'`
            if [ "$version" != "" ]; then
                perl -i -pe "s/Revision: [^\n]*/Revision: $version'/"  $FLRVERSION
                perl -i -pe "use POSIX qw(strftime); my \$date=strftime \"%F %T\", gmtime; s/Date: [^\n]*/Date: \$date'/"  $FLRVERSION
                break
            fi
        done
    fi
fi


# This is how we get revision from Smartgit-SVN integration
# for arg in `git rev-list HEAD`; do git log -1 --pretty="%B" $arg | grep git-svn-id; done
# The lines will look like
#     git-svn-id: svn+ssh://svn.code.sf.net/p/flora/src/trunk@1234 0877669f-6caa-400d-a9c2-f8e882ed57a8
# Extract @1234 from the first matching line.
