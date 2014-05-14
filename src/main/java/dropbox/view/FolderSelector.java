package dropbox.view;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class FolderSelector implements TreeSelectionListener {
	
	ConversionView view;
	JTree tree;
	ArrayList<TreePath> selectedPaths = new ArrayList<TreePath>();
	
	public FolderSelector(ConversionView view, JTree tree){
		this.view = view;
		this.tree = tree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		selectedPaths.clear();
		TreePath[] paths = tree.getSelectionPaths();
		
		
		if(paths != null){
			for(TreePath path: paths){
				boolean shouldAdd = true;

				for(int i=0; i<selectedPaths.size(); i++){
					TreePath otherPath = selectedPaths.get(i);
					if(path.isDescendant(otherPath)){
						selectedPaths.remove(i);
						i--;
					}else if(otherPath.isDescendant(path)){
						shouldAdd = false;
						break;
					}
				}
				if(shouldAdd) selectedPaths.add(path);
			}
		}
		
		view.shouldAllowTranslation(selectedPaths.size() > 0);
	}
	
	public ArrayList<String> getSelectedFolderNames(){
		
		ArrayList<String> selectedFolderNames = new ArrayList<String>();
		
		for(TreePath path: selectedPaths){
			selectedFolderNames.add(path.getLastPathComponent().toString());
		}
		
		return selectedFolderNames;
	}

}
