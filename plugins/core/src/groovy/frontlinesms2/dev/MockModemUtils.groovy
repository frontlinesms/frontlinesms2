package frontlinesms2.dev

import serial.mock.*

import net.frontlinesms.test.serial.hayes.*

import org.smslib.NotConnectedException
import org.smslib.util.*

class MockModemUtils {
	static void initialiseMockSerial(Map portIdentifiers) {
		// Set up modem simulation
		MockSerial.init();
		MockSerial.setMultipleOwnershipAllowed(true);
		portIdentifiers.each { name, cpi ->
			MockSerial.setIdentifier(name, cpi)
		}
	}
	
	static SerialPortHandler createMockPortHandler_rejectPin() {
		return new GroovyHayesPortHandler(new GroovyHayesState([error: "ERROR: 1",
				responses: ["AT+CPIN?", "+CPIN: SIM PIN", ~/AT\+CPIN="\d+"/, "+CME ERROR: SIM PIN INCORRECT"]
						+ standardResponses]))
	}

	static SerialPortHandler createMockPortHandler_badPort() {
		new GroovyHayesPortHandler(new GroovyHayesState([error: 'ERROR: 123',
				responses: ['AT', new IOException("This is a bad mock port :(")]]));
	}

	static SerialPortHandler createMockPortHandler_sendFails() {
		return createMockPortHandler(false)
	}
	
	static SerialPortHandler createMockPortHandler_disconnectOnReceive() {
		def responses = standardResponses +
				[~/AT\+CMGL=\d/, new NotConnectedException("This mock always throws this exception when asked to receive.")]
		return new GroovyHayesPortHandler(new GroovyHayesState([error: "ERROR: 1",
				responses: responses]))
	}
	
	static SerialPortHandler createMockPortHandler_withTextMessages(List text) {
		def messages = [:]
		text.eachWithIndex { it, i ->
			messages[i] = createPdu(it)
		}
		return createMockPortHandler(true, messages)
	}
	
	private static String createPdu(String messageText, String from="1234", Date date=new Date(),
			String smscNumber='5555555') {
// TODO implement following in SMS Lib
//		def pdu = SmsDeliverPdu.create(smscNumber, messageText, from, date)
//		HexUtils.encode(pdu.toBinary())

		def septets = GsmAlphabet.stringToBytes(messageText)
		def septetCount = septets.size()
		def encodedSeptets = GsmAlphabet.septetStream2octetStream(septets, 0)
		'07915892000000F0040B915274204365F7000070402132522423' + HexUtils.encode([septetCount] as byte[]) + HexUtils.encode(encodedSeptets)
	}
	
	
	static SerialPortHandler createMockPortHandler_withMessages() {
		createMockPortHandler(true, [2: '07915892000000F0040B915892214365F70000701010221555232441D03CDD86B3CB2072B9FD06BDCDA069730AA297F17450BB3C9F87CF69F7D905',
						3: '07915892000000F0040B915892214365F700007040213252242331493A283D0795C3F33C88FE06C9CB6132885EC6D341EDF27C1E3E97E7207B3A0C0A5241E377BB1D7693E72E',
						6:'07915892000000F0040B915274204365F70000704021325224230AE6F79B2E0EB3D9A030',
						7:'07915892000000F0040B915274204365F70000704021325224230D201008647C3EA9C220931906',
						8:'07915892000000F0040B915274204365F700007040213252242313E6F79B2A0CB2D920100804028140A04610',
						9:'07915892000000F0040B915274204365F70000704021325224230AE6F79B2E0EB3D92031',
						10:'07915892000000F0040B915274204365F70000704021325224230AE6F79B2E0EB3D92032',
						11:'07915892000000F0040B915274204365F700007040213252242309E6375D1C66B34161',
						12:'07915892000000F0040B915274204365F700007040213252242309E6305D1C66B34143',
						13:'07915892000000F0040B915274204365F70000704021325224230A6679982E0EB3D9203A'])
	}
	
	static SerialPortHandler createMockPortHandler(boolean canSend=true, Map receiveMessages=[:], List sentMessages=[]) {
		def state_initial = new GroovyHayesState(error: "ERROR: 1",
				responses: standardResponses + 
						[~/AT\+CMGL=\d/, { handler, request ->
							println "Hello I have been called.  What am I going to do?"
							println "I ahve been given this object: $handler"
							def s = ""
							handler.receiveMessages.each { k, v ->
								s += "+CMGL: $k,1,,${v.size()>>1}\r\n$v\r\n"
							}
							println "Created CMGL response: $s"
							return s + "\r\rOK"
						},
						~/AT\+CMGD=\d+/, { handler, request ->
							def messageId = (request =~ /\d+/)[0]
							println "deleting message: $messageId"
							handler.receiveMessages.remove(Integer.parseInt(messageId))
							println "Message are now: ${handler.receiveMessages}"
							"OK"
						}],
				// these are returned by ~/AT\+CMGL=\d/
				receiveMessages:receiveMessages)

		if(canSend) {
			def state_waitingForPdu = new GroovyHayesState(error:new GroovyHayesResponse("ERROR: 2", state_initial),
					responses:[~/.+/, { handler, request ->
						handler.sentMessages << request
						println "Added sent message [handler=$handler,request=$request]; sentMessages now $handler.sentMessages"
						new GroovyHayesResponse('+CMGS: 0\rOK', state_initial)
					}],
					sentMessages:sentMessages)
			state_initial.setResponse(~/AT\+CMGS=\d+/, new GroovyHayesResponse("OK", state_waitingForPdu))
		}

		new GroovyHayesPortHandler(state_initial)
	}
	
	private static def getStandardResponses() {
		["AT", "OK",
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
				"AT+CPMS?", '+CPMS:\r"SM",0,100\rOK',
				'AT+CPMS="ME"', "ERROR",
				'AT+CPMS="SM"', "OK"]
	}
}

