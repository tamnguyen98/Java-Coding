import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.Enumeration;
import java.util.Scanner;


public class SerialTest implements SerialPortEventListener {
  SerialPort serialPort;
  /** The port we're normally going to use. */
  private static String PORT_NAMES[] = {
      "/dev/tty.usbserial-A9007UX1", // Mac OS X
      "/dev/ttyACM0", // Raspberry Pi
      "/dev/ttyUSB0", // Linux
      "COM4", // Windows
  };
  
  int one = KeyEvent.VK_UNDEFINED, two = one, three = one, four = one;
  int b1 = KeyEvent.VK_UNDEFINED, b2 = one, b3 = one, b4 = one;

  private String usePort = "COM4";
  /**
   * A BufferedReader which will be fed by a InputStreamReader
   * converting the bytes into characters
   * making the displayed results codepage independent
   */
  private BufferedReader input;
  /** The output stream to the port */
  //private OutputStream output;
  /** Milliseconds to block while waiting for port open */
  private static final int TIME_OUT = 2000;
  /** Default bits per second for COM port. */
  private static final int DATA_RATE = 9600;

  public SerialTest(String val)
  {
    usePort = val;
    PORT_NAMES[3] = val;
  }

  public void initialize() {
    // the next line is for Raspberry Pi and
    // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
	  System.out.println("Port: " + usePort);
    System.setProperty("gnu.io.rxtx.SerialPorts", usePort);

    CommPortIdentifier portId = null;
    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

    //First, Find an instance of serial port as set in PORT_NAMES.
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
      for (String portName : PORT_NAMES) {
        if (currPortId.getName().equals(portName)) {
          portId = currPortId;
          break;
        }
      }
    }
    if (portId == null) {
      System.out.println("Could not find COM port.");
      return;
    }

    try {
      // open serial port, and use class name for the appName.
      serialPort = (SerialPort) portId.open(this.getClass().getName(),
          TIME_OUT);

      // set port parameters
      serialPort.setSerialPortParams(DATA_RATE,
          SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,
          SerialPort.PARITY_NONE);

      // open the streams
      input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
      output = serialPort.getOutputStream();

      // add event listeners
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  /**
   * This should be called when you stop using the port.
   * This will prevent port locking on platforms like Linux.
   */
  public synchronized void close() {
    if (serialPort != null) {
      serialPort.removeEventListener();
      serialPort.close();
    }
  }
  
  public void setMovement(int one, int two, int three, int four) {
	  this.one = one;
	  this.two = two;
	  this.three = three;
	  this.four = four;
	  System.out.println(usePort + " " + this.one + " "  + this.two +  " " + this.three + " " + this.four);
  }
  
  public void setButtons(int b1, int b2, int b3, int b4) {
	  this.b1 = (b1 != -1)? b1: KeyEvent.VK_UNDEFINED;
	  this.b2 = (b2 != -1)? b2: KeyEvent.VK_UNDEFINED;
	  this.b3 = (b3 != -1)? b3: KeyEvent.VK_UNDEFINED;
	  this.b4 = (b4 != -1)? b4: KeyEvent.VK_UNDEFINED;
	  System.out.println("Buttons 1-4 are mapped to (in order): ");
	  System.out.println(this.b1 + " "  + this.b2 +  " " + this.b3 + " " + this.b4);
  }

  /**
   * Handle an event on the serial port. Read the data and print it.
   */
  public synchronized void serialEvent(SerialPortEvent oEvent) {
    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      try {
        Robot robot = new Robot();
        String inputLine=input.readLine();
        System.out.println("Output: "+inputLine);
        /*
         * READ the output the arduino send out through serial
         */
        if (inputLine.equals("1")) { //if arduino is rading 1
          robot.keyPress(one);
          robot.keyRelease(two);
        }
        else if (inputLine.equals("2")) {
          robot.keyPress(two);
          robot.keyRelease(two);
        }
        else if (inputLine.equals("3")) {
            robot.keyPress(three);
            robot.keyRelease(three);
          }
        else if (inputLine.equals("4")) {
            robot.keyPress(four);
            robot.keyRelease(four);
          }
        if (inputLine.equals("5") && b1 != KeyEvent.VK_UNDEFINED) { //if arduino is rading 1
		    robot.keyPress(one);
		    robot.keyRelease(two);
        }
		else if (inputLine.equals("6") && b2 != KeyEvent.VK_UNDEFINED) {
		    robot.keyPress(two);
		    robot.keyRelease(two);
		}
		  else if (inputLine.equals("7") && b3 != KeyEvent.VK_UNDEFINED) {
		  robot.keyPress(three);
		  robot.keyRelease(three);
		}
		  else if (inputLine.equals("8") && b4 != KeyEvent.VK_UNDEFINED) {
		  robot.keyPress(four);
		  robot.keyRelease(four);
		}

      } catch (Exception e) {
        System.err.println(e.toString());
      }
    }
    // Ignore all the other eventTypes, but you should consider the other ones.
  }
  
  public static void main(String[] args) throws Exception {
	  String com = "COM";
	  System.out.println("Enter COM number: ");
	  Scanner scan = new Scanner(System.in);
	  int num = scan.nextInt();
	  com = com+num;
	  scan.nextLine();
	  
	  System.out.println("Do you want to use arrow keys? (1 for yes 0 for no)");
	  String tmp = scan.nextLine();
	  int answer = Integer.parseInt(tmp);
	  int one, two, three, four = 0;
	  int b1, b2, b3, b4 = 0;
	  one = two = three = four;
	  b1 = b2 = b3 = b4;
	  if (answer == 1) {
		  // serial layout: 1 is up and goes clockwise
		  System.out.println("Type 'up', 'right', 'down', or 'left' for the following questions.");
		  scan.nextLine();
		  one = setArrowInput(scan,1);
		  two = setArrowInput(scan,2);
		  three = setArrowInput(scan,3);
		  four = setArrowInput(scan,4);
	  }
	  else { // has to be upper case
		  System.out.println("Enter key for output 1: ");
		  one = scan.nextLine().toUpperCase().charAt(0);
		  System.out.println(" Enter key for output 2: ");
		  two = scan.nextLine().toUpperCase().charAt(0);
		  System.out.println("Enter key for output 3: ");
		  three = scan.nextLine().toUpperCase().charAt(0);
		  System.out.println("Enter key for output 4: ");
		  four = scan.nextLine().toUpperCase().charAt(0);
	  }
	  
	  /*
	   * Registering the buttons
	   */
	  System.out.println("If your controller uses buttons, make sure the output of the 4 buttons are 5-6\n");
	  System.out.println("Make sure to turn on Caps and enter -1 for outputs you dont want to use");
	  int value = 0;
	  System.out.println("Enter key for output 5: ");
	  tmp = scan.nextLine();
	  value = Integer.parseInt(tmp);
	  b1 = (value != -1)? tmp.toUpperCase().charAt(0): value;
	  
	  System.out.println(" Enter key for output 6: ");
	  tmp = scan.nextLine();
	  value = Integer.parseInt(tmp);
	  
	  b2 = (value != -1)? tmp.toUpperCase().charAt(0): value;
	  System.out.println("Enter key for output 7: ");
	  tmp = scan.nextLine();
	  value = Integer.parseInt(tmp);
	  b3 = (value != -1)? tmp.toUpperCase().charAt(0): value;
	  
	  System.out.println("Enter key for output 8: ");
	  System.out.println("Enter key for output 7: ");
	  tmp = scan.nextLine();
	  value = Integer.parseInt(tmp);
	  b4 = (value != -1)? tmp.toUpperCase().charAt(0): value;
	  
	SerialTest main = new SerialTest(com);
	main.setMovement(one, two, three, four);
	main.setButtons(b1, b2, b3, b4);
	main.initialize();
	Thread t=new Thread() {
	  public void run() {
	    //the following line will keep this app alive for 1000 seconds,
	    //waiting for events to occur and responding to them (printing incoming messages to console).
	    try {
	      Thread.sleep(1000);
	
	    } catch (InterruptedException ie) {}
	  }
	};
	t.start();
	System.out.println("Started");
  }
  static void printKey(int q) {
	  switch (q) {
	  case KeyEvent.VK_UP:
		  System.out.print("UP");
		  break;
	  case KeyEvent.VK_RIGHT:
		  System.out.print("RIGHT");
		  break;
	  case KeyEvent.VK_LEFT:
		  System.out.print("LEFT");
		  break;
	  case KeyEvent.VK_DOWN:
		  System.out.print("DOWN");
		  break;
	  default:
		  if (q >= KeyEvent.VK_A && q <= KeyEvent.VK_Z)
			  System.out.print((char)q);
		  break;
	  }
	  System.out.println();
  }
  static int setArrowInput(Scanner scan, int output) {
	  System.out.println("Enter key for output "+output+": ");
	  String one = scan.nextLine();
	  if (one.equals("up")) {
		  return KeyEvent.VK_UP;
	  }
	  else if (one.equals("right")) {
		  return KeyEvent.VK_RIGHT;
	  }
	  else if (one.equals("left")) {
		  return KeyEvent.VK_LEFT;
	  }
	  else if (one.equals("down")) {
		  return KeyEvent.VK_DOWN;
	  }
	  else
		  return setArrowInput(scan,output);
  }
}
