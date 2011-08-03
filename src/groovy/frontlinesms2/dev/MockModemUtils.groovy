package frontlinesms2.dev

import serial.mock.SerialPortHandler

import net.frontlinesms.test.serial.hayes.*

class MockModemUtils {
	static SerialPortHandler createMockPortHandler() {
		HayesState state_initial = HayesState.createState("ERROR: 1",
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
				~/AT\+CMGL=\d/, '''+CMGL: 2,1,,51
07915892000000F0040B915892214365F70000701010221555232441D03CDD86B3CB2072B9FD06BDCDA069730AA297F17450BB3C9F87CF69F7D905
+CMGL: 3,1,,62
07915892000000F0040B915892214365F700007040213252242331493A283D0795C3F33C88FE06C9CB6132885EC6D341EDF27C1E3E97E7207B3A0C0A5241E377BB1D7693E72E

OK''')
		HayesState state_waitingForPdu = HayesState.createState(new HayesResponse("ERROR: 2", state_initial),
				~/.+/, new HayesResponse('+CMGS: 0\rOK', state_initial))
		state_initial.setResponse(~/AT\+CMGS=\d+/, "OK", state_waitingForPdu)
		
		SerialPortHandler portHandler = new StatefulHayesPortHandler(state_initial);
	}
}