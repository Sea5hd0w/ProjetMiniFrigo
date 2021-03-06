package ArduinoCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Arduino implements SerialPortEventListener, IArduino{
	SerialPort serialPort;
	
	CommPortIdentifier serialPortId;
	Enumeration enumComm;
	/** The port we�re normally going to use. */
	private String portName = "";

	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	
	private String readedValue = "";

	public Arduino()
	{
		enumComm = CommPortIdentifier.getPortIdentifiers();
		this.getPort();
		this.initialize();
	}
	
	/* (non-Javadoc)
	 * @see ArduinoCommunication.IArduino#initialize()
	 */
	@Override
	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(portName)) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			System.out.println(" Could not find COM port. ");
			return;
		}

		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see ArduinoCommunication.IArduino#close()
	 */
	@Override
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/* (non-Javadoc)
	 * @see ArduinoCommunication.IArduino#serialEvent(gnu.io.SerialPortEvent)
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=null;
				if (input.ready()) {
					inputLine = input.readLine();

					String [] chunks = inputLine.split(" , ");

					//System.out.println(inputLine);
					readedValue = inputLine;
					//System.out.println(chunks[0] + " \t " + chunks[1] + " \t " + chunks[2] + " \t ");
				}

			} catch (Exception e) {
				//System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	/* (non-Javadoc)
	 * @see ArduinoCommunication.IArduino#getPort()
	 */
	@Override
	public boolean getPort() {
		serialPortId = (CommPortIdentifier)enumComm.nextElement();
		if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL)
		{
			System.out.println(serialPortId.getName());
			this.portName = serialPortId.getName();
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see ArduinoCommunication.IArduino#getReadedValue()
	 */
	@Override
	public String getReadedValue()
	{
		return readedValue;
	}

}
