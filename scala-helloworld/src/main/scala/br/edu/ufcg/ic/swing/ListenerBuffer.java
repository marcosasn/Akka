package br.edu.ufcg.ic.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ListenerBuffer implements ChangeListener {
	
	private List<Integer> numeros;
	private JLabel lblBufferstate;
	
	public ListenerBuffer(){
		numeros = new ArrayList<>();
	}
	
	public void setLabelBuffer(JLabel lblBufferstate){
		this.lblBufferstate = lblBufferstate;
	}
	
	private void showBufferStateOnUI(){
		lblBufferstate.setText(numeros.toString());
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		numeros = (List<Integer>) changeEvent.getSource();
		System.out.println("buffer mudou! " + numeros.toString());
		showBufferStateOnUI();
	}
}
