package frontlinesms2.dev

import serial.mock.*

import net.frontlinesms.test.serial.hayes.*

class MockModemUtils {
	static void initialiseMockSerial(Map portIdentifiers) {
		// Set up modem simulation
		MockSerial.init();
		MockSerial.setMultipleOwnershipAllowed(true);
		portIdentifiers.each { name, cpi ->
			MockSerial.setIdentifier(name, cpi)
		}
/*		Mockito.when(MockSerial.getMock().values()).thenReturn(Arrays.asList([cpi]));*/
	}
	
	static SerialPortHandler createMockPortHandler() {
		createMockPortHandler([2: '07915892000000F0040B915892214365F70000701010221555232441D03CDD86B3CB2072B9FD06BDCDA069730AA297F17450BB3C9F87CF69F7D905',
						3: '07915892000000F0040B915892214365F700007040213252242331493A283D0795C3F33C88FE06C9CB6132885EC6D341EDF27C1E3E97E7207B3A0C0A5241E377BB1D7693E72E'])
	}
	
	static SerialPortHandler createMockPortHandler(Map messages) {
		def state_initial = new GroovyHayesState([error: "ERROR: 1",
				responses: ["AT", "OK",
						"AT+CMEE=1", "OK",
						"AT+STSF=1", "OK",
						"AT+CPIN?", "+CPIN: READY",
						"AT+CGMI", "WAVECOM MODEM\rOK",
						"AT+CGMM", "900P\rOK",
						"AT+CNUM", '+CNUM :"Phone", "0712345678",129\rOK',
						"AT+CGSN", "123456789099998\rOK",
						"AT+CIMI", "254123456789012\rOK",
						"AT+COPS=0", "OK",
						"AT+CLIP=1", "OK",
						"ATE0", "OK",
						"AT+CREG?", "+CREG: 1,1\rOK",
						"AT+CMGF=0", "OK",
						"+++", "",
						"AT+CPMS?", "+CPMS:\r\"ME\",1,15,\"SM\",0,100\rOK",
						'AT+CPMS="ME"', "OK",
						'AT+CPMS="SM"', "OK",
						~/AT\+CMGL=\d/, { handler, request ->
							println "Hello I have been called.  What am I going to do?"
							println "I ahve been given this object: $handler"
							def s = ""
							handler.messages.each { k, v ->
								s += "+CMGL: $k,1,,${v.size()>>1}\r\n$v\r\n"
							}
							println "Created CMGL response: $s"
							return s + "\r\rOK"
						},
						~/AT\+CMGD=\d/, { handler, request ->
							def messageId = (request =~ /\d+/)[0]
							println "deleteing message: $messageId"
							handler.messages.remove(Integer.parseInt(messageId))
							println "Message are now: ${handler.messages}"
							"OK"
						}],
				// these are returned by ~/AT\+CMGL=\d/
				messages: messages])
		// TODO reinstate this state
		/*def state_waitingForPdu = HayesState.createState(new HayesResponse("ERROR: 2", state_initial),
				~/.+/, new HayesResponse('+CMGS: 0\rOK', state_initial))
		state_initial.setResponse(~/AT\+CMGS=\d+/, "OK", state_waitingForPdu)*/
		
		new GroovyHayesPortHandler(state_initial)
	}
}