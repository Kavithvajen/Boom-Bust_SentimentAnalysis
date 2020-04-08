To run corefernce program use following Command:
java -Xmx5g -cp stanford-corenlp-3.9.2.jar:stanford-corenlp-3.9.2-models.jar:sentiment-analysis-1.0-SNAPSHOT.jar TestCoref <setup_dir_path> "<Protagonist Name>"

This will pick up all the text files in input directory and run corefernce code to filter all the sentences where protagonist's name is mentioned in each file one by one. The output will be stored with same file name in output directory. 
NOTE: Since corenlp doesn't work well with huge files, its recommended to break files into 1000-2000 lines based on chapter.

Once all the files are finished and available in output directory run merged.py to merge filtered text into a single output file.

Command to merge file:
python merge.py
