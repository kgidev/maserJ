#!/bin/sh
# rename all processed Files back 
find ~/diplinfSVN/eclipseworkspace/lib/data/ -name observation.xml* -execdir mv {} observation.xml \;
