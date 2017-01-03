
# This assumes that XSB is sitting in ../XSB/ or in ./XSB

echo ""
echo "+++++ Installing Flora-2"
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
mv -f $tmpxsbdir "$xsbdir"
cd "$flrdir"
echo "+++++ Configuring Flora-2"
./floraconfig "$xsbdir/bin/xsb" selfextract >> "$currdir/flora2-install.log" 2>&1 || \
    (echo "***** Configuration of Flora-2 failed: see $currdir/flora2-install.log"; exit 1)

echo "..... The build log is in $currdir/flora2-install.log"
echo ""
echo ""
echo "+++++ All is well: you can run Flora-2 using the script"
echo "+++++    $currdir/flora2/runflora"
echo ""
