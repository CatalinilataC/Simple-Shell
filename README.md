# Simple-Shell

Implementation of a simplified shell which allows execution commands of querying and modification of a file system.
The file system will be designed as a tree structure where nodes are directories and files(leaves), each node having 0 or more sons and the root will be "/" .

The following commands are supported:
    ls (<path>)
    
    pwd
    
    cd <path>
    
    cp <source> <dest_folder>
    
    mv  <source> <dest_folder>
    
    rm <path>
    
    touch <file_path>
    
    mkdir <folder path>
    
    grep "<regex>"
