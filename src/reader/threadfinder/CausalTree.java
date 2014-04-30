package reader.threadfinder;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CausalTree<T extends CausalObject<T>> {

	
	public static class Node<T extends CausalObject<T>>{
		
		Node<T> parent;
		ArrayList<Node<T>> children = new ArrayList<Node<T>>();
		T value;
		
		public Node(T value){
			this.value = value;
		}
		
		public void addChild(Node<T> child){
			if(!children.contains(child)){
				children.add(child);
			}
		}
		
		public ArrayList<Node<T>> getChildren(){
			return children;
		}
		
		public void setParent(Node<T> parent){
			this.parent = parent;
		}
		
		public Node<T> getParent(){
			return parent;
		}
		
		public T getValue(){
			return value;
		}
		
		public boolean isCauseOf(Node<T> node){
		
			if(node == null) return false;
			
			else return getValue().isCauseOf(node.getValue());
		}
		
		
		Point p1 = new Point();
		
	}
	
	public double getDistance(){
		
		Point p1 = new Point();
		
		
		return p1.distance(3, 4);
		
	}
	
	Map<T, Node<T>> valueToNode = new HashMap<T, Node<T>>();
	Node<T> root;
	
	
	public boolean addElement(T element){
		
		Node<T> toAdd = new Node<T>(element);
		
		if(root == null){
			root = new Node<T>(element);
			return true;
		}else if(toAdd.isCauseOf(root)){
			Node<T> temp = root;
			root = toAdd;
			return addToNode(temp, toAdd);
		}else{			
			return addToNode(toAdd, root);
		}
	}
	
	private boolean addToNode(Node<T> toAdd, Node<T> node){
		
		for(Node<T> child: node.children){
			if(addToNode(toAdd, child)){
				return true;
			}
		}
		
		if(node.isCauseOf(toAdd)){
			
			for(Node<T> child: node.children){
				if(toAdd.isCauseOf(child)){
					toAdd.addChild(child);
				}
			}
			
			node.children.removeAll(toAdd.children);
			node.addChild(toAdd);
		}
		return false;
		
	}
	
}
