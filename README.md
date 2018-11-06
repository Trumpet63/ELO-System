# ELO-System
Manages files for the creation and maintainence of a rating system based on [ELO](https://en.wikipedia.org/wiki/Elo_rating_system) to facilitate tracking skill levels in competitions. This progam is designed to handle batches of individual competitors' scores in a competition where everyone is competing against each other.

Compile: `javac elocode/*`

Usage: `java elocode/ELOCode dataFile [ratingsFile] [reportFile]`
## Files and Formats
### Data File
Three columns separated by commas with rows separated by new lines.

Column 1: id - The id associated with the contestant in the system. Consists of 5 numerical digits. Used to specify a user's name faster and allow duplicate names.

Column 2: name - The contestant's name.

Column 3: score - Some numerical representation of the contestant's performance, where a higher score is better.
### Ratings File
Five columns separated by commas with rows separated by new lines.

Column 1: id

Column 2: name

Column 3: rating - The contestant's rating based on ELO. 1000 is the starting value.

Column 4: start_date - The date and time when the contestant first entered the system.

Column 5: last_updated - The date and time when the contestant last competed.
### Report File
Six columns separated by commas with rows separated by new lines.

Column 1: id

Column 2: name

Column 3: score

Column 4: rating_before - The contestant's ELO rating before processing the contents of the data file, rounded to the nearest integer.

Column 5: rating_after - The contestant's new ELO rating, rounded to the nearest integer.

Column 6: rating_change - A signed number (+/-) that is the change in rating resulting from processing the contents of the data file, rounded to the nearest tenth.
## Use Cases
### First Run With All New Contestants
You need a data file formatted as specified above. The first column is for id, but no player will have an id in the system at this time, so leave it blank. The program will produce a ratings file (like ratings.csv) and a report file (like report_####.csv).

Example run in Windows command line:
```
C:\test>dir
 Directory of C:\test

11/05/2018  01:55 PM    <DIR>          .
11/05/2018  01:55 PM    <DIR>          ..
11/05/2018  01:45 PM               204 data.csv
11/05/2018  01:58 PM    <DIR>          elocode
               1 File(s)            204 bytes

C:\test>type data.csv
id,name,score
,player 1,5.9
,player 2,16.8
,player 3,17
,player 4,40.8
,player 5,21.2
,player 6,19.7
,player 7,13.8
,player 8,21.7
,player 9,39.5
,player 10,21.1
,player 11,5.1
,player 12,34.3

C:\test>java elocode/ELOCode data.csv

C:\test>dir
 Directory of C:\test

11/05/2018  02:04 PM    <DIR>          .
11/05/2018  02:04 PM    <DIR>          ..
11/05/2018  01:45 PM               204 data.csv
11/05/2018  02:03 PM    <DIR>          elocode
11/05/2018  02:04 PM               933 ratings.csv
11/05/2018  02:04 PM               489 report_20181105140438.csv
               3 File(s)          1,626 bytes

C:\test>type report_20181105140438.csv
id,name,score,rating_before,rating_after,rating_change
58873,player 4,40.8,1000,1091,+91.3
11073,player 8,21.7,1000,1001,+1.4
45908,player 9,39.5,1000,1085,+85.1
91389,player 6,19.7,1000,992,-8.0
57839,player 7,13.8,1000,964,-35.8
15227,player 11,5.1,1000,923,-76.7
91050,player 10,21.1,1000,999,-1.5
98495,player 1,5.9,1000,927,-73.0
84508,player 12,34.3,1000,1061,+60.7
26734,player 3,17.0,1000,979,-20.7
26935,player 2,16.8,1000,978,-21.7
85680,player 5,21.2,1000,999,-1.0

C:\test>type ratings.csv
id,name,rating,start_date,last_updated
58873,player 4,1091.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
11073,player 8,1001.3725490196078,2018/11/05 14:04:38,2018/11/05 14:04:38
45908,player 9,1085.137254901961,2018/11/05 14:04:38,2018/11/05 14:04:38
91389,player 6,991.9607843137255,2018/11/05 14:04:38,2018/11/05 14:04:38
57839,player 7,964.1960784313726,2018/11/05 14:04:38,2018/11/05 14:04:38
15227,player 11,923.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
91050,player 10,998.5490196078431,2018/11/05 14:04:38,2018/11/05 14:04:38
98495,player 1,927.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38
84508,player 12,1060.6666666666667,2018/11/05 14:04:38,2018/11/05 14:04:38
26734,player 3,979.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
26935,player 2,978.313725490196,2018/11/05 14:04:38,2018/11/05 14:04:38
85680,player 5,999.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38
```
### Additional Runs With Some New Contestants
You will need a data file as descibed above, and a ratings file from a previous run (or one you've made yourself). Scores for contestants that are already in the system need only include the id with the score. If you include a name and an id for a contestant already in the system, the name will be ignored. Scores for contestants that are not yet in the system must not have an id (since those should only be assigned by the program), and must include a name. The program will update the ratings file specified, and produce a report file.

Note: The progam writes the new ratings file to a file called `eloTempFile.csv` but renames it after deleting the old file. If you find the temp file is still there after the program finishes, something has gone wrong!

Example run in Windows command line:
```
C:\test>dir
 Directory of C:\Users\spren\Documents\test

11/05/2018  02:25 PM    <DIR>          .
11/05/2018  02:25 PM    <DIR>          ..
11/05/2018  01:45 PM               204 data.csv
11/05/2018  02:28 PM               125 data1.csv
11/05/2018  02:03 PM    <DIR>          elocode
11/05/2018  02:04 PM               933 ratings.csv
11/05/2018  02:04 PM               489 report_20181105140438.csv
               4 File(s)          1,751 bytes

C:\test>type data1.csv
id,name,score
26935,,16.8
91389,,19.7
57839,,13.8
,player 13,30
91050,,21.1
15227,,5.1
,player 14,26.7
,player 15,8.3

C:\test>type ratings.csv
id,name,rating,start_date,last_updated
58873,player 4,1091.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
11073,player 8,1001.3725490196078,2018/11/05 14:04:38,2018/11/05 14:04:38
45908,player 9,1085.137254901961,2018/11/05 14:04:38,2018/11/05 14:04:38
91389,player 6,991.9607843137255,2018/11/05 14:04:38,2018/11/05 14:04:38
57839,player 7,964.1960784313726,2018/11/05 14:04:38,2018/11/05 14:04:38
15227,player 11,923.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
91050,player 10,998.5490196078431,2018/11/05 14:04:38,2018/11/05 14:04:38
98495,player 1,927.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38
84508,player 12,1060.6666666666667,2018/11/05 14:04:38,2018/11/05 14:04:38
26734,player 3,979.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
26935,player 2,978.313725490196,2018/11/05 14:04:38,2018/11/05 14:04:38
85680,player 5,999.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38

C:\test>java elocode/ELOCode data1.csv ratings.csv

C:\test>dir
 Directory of C:\Users\spren\Documents\test

11/05/2018  02:33 PM    <DIR>          .
11/05/2018  02:33 PM    <DIR>          ..
11/05/2018  01:45 PM               204 data.csv
11/05/2018  02:28 PM               125 data1.csv
11/05/2018  02:03 PM    <DIR>          elocode
11/05/2018  02:33 PM             1,161 ratings.csv
11/05/2018  02:04 PM               489 report_20181105140438.csv
11/05/2018  02:33 PM               342 report_20181105143305.csv
               5 File(s)          2,321 bytes

C:\test>type report_20181105143305.csv
id,name,score,rating_before,rating_after,rating_change
00858,player 15,8.3,1000,953,-46.6
44253,player 13,30.0,1000,1046,+46.0
91389,player 6,19.7,992,997,+5.0
57839,player 7,13.8,964,954,-10.0
15227,player 11,5.1,923,891,-32.3
91050,player 10,21.1,999,1007,+8.5
11819,player 14,26.7,1000,1032,+31.9
26935,player 2,16.8,978,976,-2.4

C:\test>type ratings.csv
id,name,rating,start_date,last_updated
58873,player 4,1091.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
11073,player 8,1001.3725490196078,2018/11/05 14:04:38,2018/11/05 14:04:38
45908,player 9,1085.137254901961,2018/11/05 14:04:38,2018/11/05 14:04:38
91389,player 6,996.9338160158144,2018/11/05 14:04:38,2018/11/05 14:33:05
57839,player 7,954.1642952780961,2018/11/05 14:04:38,2018/11/05 14:33:05
15227,player 11,890.9106436329806,2018/11/05 14:04:38,2018/11/05 14:33:05
91050,player 10,1007.0842816000835,2018/11/05 14:04:38,2018/11/05 14:33:05
98495,player 1,927.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38
84508,player 12,1060.6666666666667,2018/11/05 14:04:38,2018/11/05 14:04:38
26734,player 3,979.2549019607843,2018/11/05 14:04:38,2018/11/05 14:04:38
26935,player 2,975.9138860101418,2018/11/05 14:04:38,2018/11/05 14:33:05
85680,player 5,999.0196078431372,2018/11/05 14:04:38,2018/11/05 14:04:38
00858,player 15,953.391417977824,2018/11/05 14:33:05,2018/11/05 14:33:05
44253,player 13,1045.9780846444905,2018/11/05 14:33:05,2018/11/05 14:33:05
11819,player 14,1031.8980846444906,2018/11/05 14:33:05,2018/11/05 14:33:05
```
