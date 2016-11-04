package br.edu.ufcg.ic.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor.ProdutorApi.Pausar;
import br.edu.ufcg.ic.akka.java.faulttolerance.Consumidor.ConsumidorApi.TempoEspera;
import br.edu.ufcg.ic.akka.java.faulttolerance.Consumidor;
import br.edu.ufcg.ic.akka.java.faulttolerance.Produtor;
import br.edu.ufcg.ic.akka.java.faulttolerance.BufferService;
import br.edu.ufcg.ic.akka.java.faulttolerance.Buffer.BufferApi.GenerateBufferFailure;

import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ProdutorConsumidorUIFaultTolerance {

	private JFrame frame;
	private JTextField textField_espera_producao;
	private JTextField textField_espera_consumo;
	private JTextField textField_capacidade_buffer;
	
	private ActorSystem system;
	private ActorRef produtor;
	private ActorRef consumidor;
	private ActorRef bufferService;
	private ListenerBuffer listenerBuffer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProdutorConsumidorUIFaultTolerance window = new ProdutorConsumidorUIFaultTolerance();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ProdutorConsumidorUIFaultTolerance() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		
		JPanel panel_1 = new JPanel();
		
		JLabel lblTempoDeEspera = new JLabel("Tempo de espera consumo");
		panel_1.add(lblTempoDeEspera);
		
		textField_espera_consumo = new JTextField();
		textField_espera_consumo.setColumns(10);
		panel_1.add(textField_espera_consumo);
		
		JPanel panel_2 = new JPanel();
		
		JLabel lblCapacidadeDoBuffer = new JLabel("Capacidade do buffer");
		panel_2.add(lblCapacidadeDoBuffer);
		
		textField_capacidade_buffer = new JTextField();
		textField_capacidade_buffer.setColumns(10);
		panel_2.add(textField_capacidade_buffer);
		
		JPanel panel_3 = new JPanel();
		
		JPanel panel_4 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_4, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_4, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JLabel lblBufferstate = new JLabel("[]");
		listenerBuffer = new ListenerBuffer();
		listenerBuffer.setLabelBuffer(lblBufferstate);
		panel_4.add(lblBufferstate);
		
		JButton btnStopConsumidor = new JButton("Pause");
		btnStopConsumidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnStopConsumidor.getText().equals("Pause")){
					btnStopConsumidor.setText("Resume");
				}else{
					btnStopConsumidor.setText("Pause");
				}
				System.out.println("Mandando mensagem para pausar o consumidor");
				consumidor.tell(new Pausar(), ActorRef.noSender());			
			}
		});
		panel_1.add(btnStopConsumidor);
		
		JButton btnExecutar = new JButton("Executar");
		btnExecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		panel_3.add(btnExecutar);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consumidor.tell(PoisonPill.getInstance(), ActorRef.noSender());
				produtor.tell(PoisonPill.getInstance(), ActorRef.noSender());
				bufferService.tell(PoisonPill.getInstance(), ActorRef.noSender());
				system.shutdown();
			}
		});
		panel_3.add(btnStop);
		
		JButton btnGerarFalha = new JButton("Gerar falha");
		btnGerarFalha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bufferService.tell(new GenerateBufferFailure(), ActorRef.noSender());
			}
		});
		panel_3.add(btnGerarFalha);
		
		JLabel lblNewLabel = new JLabel("Tempo de espera produção");
		panel.add(lblNewLabel);
		
		textField_espera_producao = new JTextField();
		panel.add(textField_espera_producao);
		textField_espera_producao.setColumns(10);
		
		JButton btnStopProdutor = new JButton("Pause");
		btnStopProdutor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnStopProdutor.getText().equals("Pause")){
					produtor.tell(new Pausar(), ActorRef.noSender());				
					btnStopProdutor.setText("Resume");
				}else{
					produtor.tell(new Pausar(), ActorRef.noSender());				
					btnStopProdutor.setText("Pause");
				}
			}
		});
		panel.add(btnStopProdutor);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	private void execute() {
		System.out.println("Espera produção " + textField_espera_producao.getText().toString());
		System.out.println("Espera consumo " + textField_espera_consumo.getText().toString());
		System.out.println("Capacidade buffer " + Integer.parseInt(textField_capacidade_buffer.getText().toString()));
		
		Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" + "akka.actor.debug.lifecycle = on");
		system = ActorSystem.create("SystemProdutorConsumidor", config);
		produtor = system.actorOf(Props.create(Produtor.class),"produtor");
		consumidor = system.actorOf(Props.create(Consumidor.class),"consumidor");
		produtor.tell(new TempoEspera(Integer.parseInt(textField_espera_producao.getText().toString())),
				ActorRef.noSender());
		consumidor.tell(new TempoEspera(Integer.parseInt(textField_espera_consumo.getText().toString())),
				ActorRef.noSender());
		
		bufferService = system.actorOf(Props.create(BufferService.class, produtor, consumidor, 
				Integer.parseInt(textField_capacidade_buffer.getText().toString()),
				listenerBuffer), "bufferService");
		bufferService.tell(BufferService.Start, ActorRef.noSender());
	}
}
