
# This assumes that XSB is sitting in ../XSB/ or in ./XSB

echo ""
echo "+++++ Installing Flora-2 -- will take a few minutes"
echo ""

currdir=`pwd`

if [ -d ./flora2 -a -d ./XSB ]; then
    flrdir="$currdir/flora2"
    xsbdir="$currdir/XSB"
    tmpxsbdir=/tmp/XSB-`date +"%y-%m-%d-%H_%M_%S"`
    rm -rf $tmpxsbdir || \
	(echo "***** You have no write access to the /tmp folder: your system is misconfigured"; echo "***** Installation has failed";  exit 1)
else
    echo "***** This script is to be run in a folder that contains ./XSB & ./flora2"
    exit 1
fi

# Move XSB to /tmp to sidestep the problems with configuring it
# in dirs that have spaces
mv -f "$xsbdir" $tmpxsbdir
cd $tmpxsbdir/build

echo "+++++ Configuring XSB"
./configure --with-dbdrivers > "$currdir/flora2-install.log" 2>&1 || \
    (echo "***** Configuration of XSB failed: see $currdir/flora2-install.log"; exit 1)
echo "+++++ Compiling XSB"
./makexsb >> "$currdir/flora2-install.log" 2>&1 || \
    (echo "***** Compilation of XSB failed: see $currdir/flora2-install.log"; exit 1)

# Move compiled XSB from /tmp to its intended place
# Splitting mv into cp+rm because of what seems to be a bug in Ubuntu over W10
cp -rf $tmpxsbdir "$xsbdir"
rm -rf $tmpxsbdir

cd "$flrdir"
echo "+++++ Configuring Flora-2"
./floraconfig "$xsbdir/bin/xsb" selfextract >> "$currdir/flora2-install.log" 2>&1 || \
    (echo "***** Configuration of Flora-2 failed: see $currdir/flora2-install.log"; exit 1)

# setting up the icons
if [ "`uname`" = "Linux" -a -d $HOME/Desktop ]; then
    cat "$currdir/flora2/admin/linux/flora2-desktop" | sed "s|FLORA2_BASE_FOLDER|$currdir|" > $HOME/Desktop/Flora2Engine.desktop
    chmod u+x $HOME/Desktop/Flora2Engine.desktop
fi
if [ "`uname`" = "Darwin" -a -d $HOME/Desktop ]; then

    if [ "`which Rez`" = "" ]; then
        echo
        echo
        echo !!! Mac Developer Tools do not seem to be installed.
        echo !!! As a result, desktop icons might not get installed
        echo !!! and some Flora-2 packages might be unavailable
        echo
        /bin/echo -n I understand...
        read response
        echo
    fi

    /bin/rm -f "$HOME/Desktop/Flora-2 Engine"

    # Set up runflora desktop shortcut
    # the following multi-line kludge is to set up a custom icon
    echo "Running sips -i flora-desktop.png" >> "$currdir/flora2-install.log"
    sips -i "$currdir/flora2/etc/flora-desktop.png"  >> "$currdir/flora2-install.log" 2>&1 || echo
    echo "Running DeRez -only icns, Rez -append, SetFile -a C" >> "$currdir/flora2-install.log"
    DeRez -only icns "$currdir/flora2/etc/flora-desktop.png" > $currdir/tmpicns.rsrc 2>> "$currdir/flora2-install.log" || echo
    Rez -append $currdir/tmpicns.rsrc -o "$currdir/flora2/runflora" >> "$currdir/flora2-install.log" 2>&1 || echo

    SetFile -a C "$currdir/flora2/runflora" >> "$currdir/flora2-install.log" 2>&1 || echo
    /bin/rm -f $currdir/tmpicns.rsrc

    # make desktop alias
    echo "Running mk-alias 'Flora-2 Engine'" >> "$currdir/flora2-install.log"
    sh "$currdir/flora2/admin/mac/mk-alias" "$currdir/flora2/runflora" "Flora-2 Engine" >> "$currdir/flora2-install.log" 2>&1
fi


echo "..... The build log is in $currdir/flora2-install.log"
echo ""
echo ""
echo "+++++ All is well: you can run Flora-2 using the script"
echo "+++++    $currdir/flora2/runflora"
echo ""
