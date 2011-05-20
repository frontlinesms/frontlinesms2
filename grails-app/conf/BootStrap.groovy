import grails.util.Environment
import frontlinesms2.*
import org.mockito.Mockito
import java.lang.reflect.Field
import serial.mock.MockSerial
import serial.mock.SerialPortHandler
import serial.mock.CommPortIdentifier
import net.frontlinesms.test.serial.HayesPortHandler

class BootStrap {

	def init = { servletContext ->
		if (Environment.current == Environment.DEVELOPMENT) {
          	// do custom init for dev here
			['Friends', 'Listeners', 'Not Cats', 'Adults'].each() { createGroup(it) }
			def alice = createContact("Alice", "+123456789")
			def friends = Group.findByName('Friends')
			def notCats = Group.findByName('Not Cats')
//			Contact.findAll().each() { Group.findByName('Friends').addToMembers(it) }
			createContact("Bob", "+198765432")
			Contact.findAll().each() {
				GroupMembership.create(it, friends)
				GroupMembership.create(it, notCats)
			}
//			Contact.findAll().each() { Group.findByName('Not Cats').addToMembers(it) }
			createContact("Kate", "+198730948")

			new EmailFconnection(name:"mr testy's email", protocol:EmailProtocol.IMAPS, serverName:'imap.zoho.com',
					serverPort:993, username:'mr.testy@zoho.com', password:'mister').save(failOnError:true)

			initialiseMockSerialDevice()
			new SmslibFconnection(name:"COM99 mock smslib device", port:'COM99', baud:9600).save(failOnError:true)
			
			[new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
					new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
					new Fmessage(src:'Joe', dst:'+254112233', text:'pantene is the best')].each() {
						it.inbound = true
						it.save(failOnError:true)
					}

			[new Poll(title:'Football Teams', responses:[new PollResponse(value:'manchester'),
					new PollResponse(value:'barcelona')]),
					new Poll(title:'Shampoo Brands', responses:[new PollResponse(value:'pantene'),
					new PollResponse(value:'oriele')])].each() {
						it.save(failOnError:true, flush:true)
					}

			PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob'))
			PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice'))
			PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))
		}
	}


	def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}

	def createContact(String n, String a) {
		def c = new Contact(name: n, address: a)
		c.save(failOnError: true)
	}

	def destroy = {
	}

	def initialiseMockSerialDevice() {
		// Set up modem simulation
		MockSerial.init();
		MockSerial.setMultipleOwnershipAllowed(true);
		SerialPortHandler portHandler = new HayesPortHandler("ERROR: 999",
				"AT", "OK",
				"AT+CMEE=1", "OK",
				"AT+STSF=1", "OK",
				"AT+CPIN?", "+CPIN: READY",
				"AT+CGMI", "WAVECOM MODEM\rOK",
				"AT+CGMM", "900P\rOK",
				"AT+CNUM", "+CNUM :\"Phone\", \"0712345678\",129\rOK",
				"AT+CGSN", "123456789099998\rOK",
				"AT+CIMI", "254123456789012\rOK",
				//"AT+CBC"
				"AT+COPS=0", "OK",
				"AT+CLIP=1", "OK",
				"ATE0", "OK",
				"AT+CREG?", "+CREG: 1,1\rOK",
				"AT+CPMS?", "+CPMS: \"SM\",3, 10,\"SM\",3,10\rOK",
				"AT+CMGF=0", "OK",
				"+++", "", // switch 2 command mode
				"AT+CPMS?", "+CPMS:\r\"ME\",1,15,\"SM\",0,100\rOK", // get storage locations
				"AT+CPMS=\"ME\"", "OK",
				"AT+CMGL=0", '''+CMGL: 2,1,,51
07915892000000F0040B915892214365F70000701010221555232441D03CDD86B3CB2072B9FD06BDCDA069730AA297F17450BB3C9F87CF69F7D905
+CMGL: 3,1,,62
07915892000000F0040B915892214365F700007040213252242331493A283D0795C3F33C88FE06C9CB6132885EC6D341EDF27C1E3E97E7207B3A0C0A5241E377BB1D7693E72E

OK''');
		CommPortIdentifier cpi = new CommPortIdentifier("COM99", portHandler);
		MockSerial.setIdentifier("COM99", cpi);
		Mockito.when(MockSerial.getMock().values()).thenReturn(Arrays.asList([cpi]));
	}
}
