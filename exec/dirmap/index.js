const fs = require('fs');
const path = require('path');

function processDirMap(dirMapContent, dir) {
  const rootPath = dir; // Current working directory
   const lines = dirMapContent.split('\n');
 
   const rootFolder = {
     type: 'folder',
     name: '.',
     children: [],
   };
 
   let currentFolder = rootFolder;
 
	lines.forEach(line => {
	    const trimmedLine = line.trim();
	
	    if (trimmedLine !== '') {
	      const indentation = line.search(/\S/);
	      const isDirectory = trimmedLine.endsWith('/');
	      const fileName = isDirectory ? trimmedLine.slice(0, -1) : trimmedLine;
	      const item = {
	        type: isDirectory ? 'folder' : 'file',
	        name: fileName,
	      };
		  if(isDirectory) item.children = [];
	      if (indentation === 0) {
	        currentFolder.children.push(item);
	        currentFolder = item;
	      } else {
	        let parent = currentFolder;
	        while (indentation <= parent.indentation) {
	          parent = parent.parent;
	        }
	        parent.children.push(item);
	        item.indentation = indentation;
	        item.parent = parent;
	        currentFolder = item;
	      }
	    }
	  });
	

  createStructure(rootFolder, rootPath);
}

function createStructure(rootFolder, basePath = '') {
  const currentPath = path.join(basePath, rootFolder.name);
  createDirectory(currentPath);

  rootFolder.children.forEach(child => {
    if (child.type === 'folder') {
      createStructure(child, currentPath);
    } else if (child.type === 'file') {
      const filePath = path.join(currentPath, child.name);
      createFile(filePath);
    }
  });
}

function createDirectory(directoryPath) {
  if (!fs.existsSync(directoryPath)) {
    fs.mkdirSync(directoryPath, { recursive: true });
    console.log('Created directory:', directoryPath);
  }
}

function createFile(filePath) {
  if (!fs.existsSync(filePath)) {
    fs.writeFileSync(filePath, '');
    console.log('Created file:', filePath);
  }
}

function findDirmapInDir(dir){
	const dirmap = path.join(dir, '.dirmap');
	if(fs.existsSync(dirmap)){
		processDirMap(fs.readFileSync(dirmap).toString(), dir);
	}
}

// processDirMap(dirMapContent);
findDirmapInDir(process.cwd());