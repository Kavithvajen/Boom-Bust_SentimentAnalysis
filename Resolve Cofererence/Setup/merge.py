from os import listdir
from os.path import isfile, join
mypath = os.path.dirname(os.path.realpath(__file__))
filenames = [f for f in listdir(mypath) if isfile(join(mypath, f))]

print(filenames)
  
# Open file3 in write mode 
with open('merged.txt', 'w') as outfile: 
  
    # Iterate through list 
    for names in filenames: 
  
        # Open each file in read mode 
        with open(names) as infile: 
  
            # read the data from file1 and 
            # file2 and write it in file3 
            outfile.write(infile.read()) 
  
        # Add '\n' to enter data of file2 
        # from next line 
        outfile.write("\n") 