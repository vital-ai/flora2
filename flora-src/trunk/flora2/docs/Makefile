## File:      flora2/docs/Makefile
##
## Author(s): Michael Kifer
##
## Contact:   flora-users@lists.sourceforge.net
##
## Copyright (C) by
##      The Research Foundation of the State University of New York, 1999-2013.
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##      http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
##

ERGOSWITCH = ../ergoisms/ergo.switch

all:  flora2-manual.pdf flora-packages.pdf ergo-manual.pdf ergo-packages.pdf

flora2-manual.dvi: flora2-manual.tex $(ERGOSWITCH)
	@echo
	@echo Running LaTeX on flora2-manual.tex
	latex flora2-manual
	@echo
	@echo Making bibliography...
	bibtex flora2-manual
	@echo
	@echo Making index...
	makeindex flora2-manual
	@echo
	@echo Recompiling...
	latex flora2-manual
	latex flora2-manual
	latex flora2-manual

flora2-manual.pdf: flora2-manual.dvi
	@echo
	@echo Making pdf for flora2-manual
	dvipdf flora2-manual

flora-packages.dvi: flora-packages.tex pkg-flora-persistence.tex pkg-flora-xml.tex pkg-java-flora.tex $(ERGOSWITCH)
	@echo
	@echo Running LaTeX on flora-packages.tex
	latex flora-packages
	bibtex flora-packages
	latex flora-packages
	latex flora-packages
	makeindex flora-packages

flora-packages.pdf: flora-packages.dvi
	@echo
	@echo Making pdf for flora-packages
	dvipdf flora-packages

ergo-manual.pdf: flora2-manual.pdf
	cp flora2-manual.pdf ergo-manual.pdf

ergo-packages.pdf: flora-packages.pdf
	cp flora-packages.pdf ergo-packages.pdf

$(ERGOSWITCH):
	@echo "" > /dev/null

clean:
	/bin/rm -f  *.bak *~ .*~ .#* *.aux *.toc *.ind *.dvi *.bbl *.blg *.log
	/bin/rm -f  *.out *.idx *.ilg

veryclean: clean
	/bin/rm -f  *.aux *.bbl *.blg *.dvi *.idx *.ilg *.ind *.log \
			*.out flora*.pdf *.ps *.toc
