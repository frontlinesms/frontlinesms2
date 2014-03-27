import java.lang.reflect.Field
import java.text.DateFormat
import java.text.SimpleDateFormat

import grails.util.Environment

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils
import net.frontlinesms.test.serial.hayes.*

import serial.SerialClassFactory
import serial.mock.MockSerial
import serial.mock.SerialPortHandler
import serial.mock.CommPortIdentifier

import org.mockito.Mockito
import grails.converters.JSON

import org.codehaus.groovy.grails.commons.ApplicationHolder

class CoreBootStrap {
	def applicationContext
	def appInfoService
	def appSettingsService
	def grailsApplication
	def deviceDetectionService
	def failPendingMessagesService
	def fconnectionService
	def localeResolver
	def camelContext
	def quartzScheduler
	def systemNotificationService

	def bootstrapData = System.properties['frontlinesms2.bootstrap.data']? Boolean.parseBoolean(System.properties['frontlinesms2.bootstrap.data']):
			Environment.current == Environment.DEVELOPMENT
	
	def init = { servletContext ->
		println "BootStrap.init() : Env=${Environment.current}"
		initialiseSerial()
		MetaClassModifiers.addAll()

		initAppSettings()
		updateAvailableFconnections()

		if(Environment.current == Environment.TEST) {
			quartzScheduler.start()
			dev_disableSecurityFilter()
			// never show new popup during tests
			appSettingsService['newfeatures.popup.show.immediately'] = false
			//default routing in tests is to use any available connections
			appSettingsService.set('routing.use', 'uselastreceiver')
			appSettingsService.set('routing.preferences.edited', true)
		}

		if(Environment.current == Environment.DEVELOPMENT) {
			//DB Viewer
			//org.hsqldb.util.DatabaseManager.main()
			// do custom init for dev here

			// Uncomment the following line to enable tracing in Camel.
			// N.B. this BREAKS AUTODISCONNECT OF FAILED ROUTES in camel
			// 2.5.0 (which we are currently using), but has been fixed
			// by camel 2.9.0 so this can be permanently enabled once we
			// upgrade our Camel dependencies.
			//camelContext.tracing = true
			dev_disableSecurityFilter()
			updateFeaturePropertyFileValues()
		}

		if(bootstrapData) {
			dev_initSmartGroups()
			dev_initGroups()
			dev_initContacts()
			dev_initFconnections()
			dev_initTextMessages()
			dev_initMissedCalls()
			dev_initPolls()
			dev_initAutoreplies()
			dev_initAutoforwards()
			dev_initFolders()
			dev_initAnnouncements()
			dev_initSubscriptions()
			dev_initWebconnections()
			dev_initCustomActivities()
			dev_initLogEntries()
			setDefaultMessageRoutingPreferences()
		}

		if(Environment.current == Environment.PRODUCTION) {
			createWelcomeNote()
			updateFeaturePropertyFileValues()
			setDefaultMessageRoutingPreferences()
		}

		setCustomJSONRenderers()
		ensureResourceDirExists()
		deviceDetectionService.init()
		failPendingMessagesService.init()
		CoreAppInfoProviders.registerAll(appInfoService)
		activateActivities()
		initialiseNonSmslibFconnections()
		println '\\o/ FrontlineSMS started.'
	}

	def destroy = {
	}

	private def initAppSettings() {
		appSettingsService.init()
		def language = appSettingsService.get('language')
		if(language) {
			def defaultLocale = new Locale(language)
			java.util.Locale.setDefault(defaultLocale)
			localeResolver.setDefaultLocale(defaultLocale)
		}
	}
	
	private def createWelcomeNote() {
		if(!SystemNotification.count()) {
			systemNotificationService.create(code:'frontlinesms.welcome')
		}
	}
	
	/** Initialise SmartGroup domain objects for development and demos. */
	private def dev_initContacts() {
		if(!bootstrapData) return
		def alice = createContact("Alice", "+123456789")
		def friends = Group.findByName('Friends')
		def notCats = Group.findByName('Not Cats')
		def bob = createContact("Bob", "+198765432")
		Contact.findAll().each() {
			it.addToGroups(friends)
			it.addToGroups(notCats)
		}
		createContact("Kate", "+198730948")

		(1..101).each {
			new Contact(name:"test-${it}", mobile:"number-${it}").save(failOnError:true)
		}
		
		[new CustomField(name: 'lake', value: 'Victoria', contact: alice),
				new CustomField(name: 'town', value: 'Kusumu', contact: bob)].each() {
			it.save(failOnError:true, flush:true)
		}
	}
	
	private def dev_initSmartGroups() {
		if(!bootstrapData) return
		new SmartGroup(name:'Kenyans', mobile:'+254').save(failOnError:true)
		new SmartGroup(name:'Test Contacts', contactName:'test-').save(failOnError:true)
	}
	
	private def dev_initGroups() {
		if(!bootstrapData) return
		['Friends', 'Listeners', 'Not Cats', 'Adults'].each() { createGroup(it) }
	}

	private def dev_initMissedCalls() {
		if(!bootstrapData) return
		for(i in 1..20) {
			new MissedCall(src:"+1234567$i", date: new Date() - i).save()
		}
	}
	
	private def dev_initTextMessages() {
		if(!bootstrapData) return

		def m5 = new TextMessage(src:'Jinja', date:new Date(), text:'Look at all my friends!')
		for(i in 1..100) m5.addToDispatches(dst:"+12345678$i", status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true)
		for(i in 101..200) m5.addToDispatches(dst:"+12345678$i", status:DispatchStatus.FAILED).save(failOnError:true)
		for(i in 201..300) m5.addToDispatches(dst:"+12345678$i", status:DispatchStatus.PENDING).save(failOnError:true)

		for(i in 1..100) {
			new TextMessage(src:"123456", date:new Date(), text:"Generated SENT message: $i")
					.addToDispatches(dst:"+12345678$i", status:DispatchStatus.SENT, dateSent:new Date())
					.save(failOnError:true)
		}

		for(i in 101..200) {
			new TextMessage(src:"123456", date:new Date(), text:"Generated PENDING message: $i")
					.addToDispatches(dst:"+12345678$i", status:DispatchStatus.PENDING)
					.save(failOnError:true)
		}

		for(i in 201..300) {
			new TextMessage(src:"123456", date:new Date(), text:"Generated FAILED message: $i")
					.addToDispatches(dst:"+12345678$i", status:DispatchStatus.FAILED)
					.save(failOnError:true)
		}

		new TextMessage(src:'+123987123',
				text:'A really long message which should be beautifully truncated so we can all see what happens in the UI when truncation is required.',
				inbound:true,
				date: new Date()).save(failOnError:true)
				
		[new TextMessage(src:'+123456789', text:'manchester rules!', date:new Date()),
				new TextMessage(src:'+198765432', text:'go manchester', date:new Date()),
				new TextMessage(src:'Joe', text:'pantene is the best', date:new Date()-1),
				new TextMessage(src:'Jill', text:"where's the hill?", date:createDate("2011/01/21")),
				new TextMessage(src:'+254675334', text:"where's the pale?", date:createDate("2011/01/20")),
				new TextMessage(src:'Humpty', text:"where're the king's men?", starred:true, date:createDate("2011/01/23"))].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
		
		(1..101).each {
			new TextMessage(src:'+198765432', text:"text-${it}", date: new Date() - it, inbound:true).save(failOnError:true)
		}

		def m1 = new TextMessage(src: '+3245678', date: new Date(), text: "time over?")
		def m2 = new TextMessage(src: 'Johnny', date:new Date(), text: "I am in a meeting")
		def m3 = new TextMessage(src: 'Sony', date:new Date(), text: "Hurry up")
		def m4 = new TextMessage(src: 'Jill', date:new Date(), text: "Some cool characters: कञॠ, and more: á é í ó ú ü ñ ¿ ¡ ºª")
		
		m1.addToDispatches(dst:'+123456789', status:DispatchStatus.FAILED)
		m1.addToDispatches(dst:'+254114533', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError: true)
		m2.addToDispatches(dst:'+254114433', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError: true)
		m3.addToDispatches(dst:'+254116633', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError: true)
		m4.addToDispatches(dst:'+254115533', status:DispatchStatus.PENDING).save(failOnError:true)

		new TextMessage(src:'+33445566', text:"modem message", inbound:true, date: new Date()).save(failOnError:true, flush:true)
		new TextMessage(src:'+33445566', text:"<0_O> marvel at the HTML & how it works!", inbound:true, date: new Date()).save(failOnError:true, flush:true)
	}
	
	private def dev_initFconnections() {
		if(!bootstrapData) return
		new EmailFconnection(name:"mr testy's email", receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
				serverPort:993, username:'mr.testy@zoho.com', password:'mister').save(failOnError:true)
		new ClickatellFconnection(name:"Clickatell Mock Server", apiId:"api123", username:"boris", password:"top secret").save(failOnError:true)
		new IntelliSmsFconnection(name:"IntelliSms Mock connection", sendEnabled:true, username:"johnmark", password:"pass_word").save(failOnError:true)
	}

	private def dev_initRealSmslibFconnections() {
		if(!bootstrapData) return
		new SmslibFconnection(name:"Huawei Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234', enabled:false).save(failOnError:true)
		new SmslibFconnection(name:"COM4", port:'COM4', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"Alex's Modem", port:'/dev/ttyUSB0', baud:9600, pin:'5602').save(failOnError:true)
		new SmslibFconnection(name:"MobiGater Modem", port:'/dev/ttyACM0', baud:9600, pin:'1149').save(failOnError:true)
		new SmssyncFconnection(name:"SMSSync connection", secret:'secret', enabled:false).save(flush: true, failOnError:true)
		new FrontlinesyncFconnection(name:"FrontlineSync connection", secret:'3469', enabled:true).save(flush: true, failOnError:true)
		new SmslibFconnection(name:"Geoffrey's Modem", port:'/dev/ttyUSB0', baud:9600, pin:'1149').save(failOnError:true)
		
	}

	private def dev_initMockSmslibFconnections() {
		if(!bootstrapData) return
		new SmslibFconnection(name:"MOCK95: rejects all pins", pin:'1234', port:'MOCK95', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"MOCK96: breaks on receive", port:'MOCK96', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"MOCK97: bad port", port:'MOCK97', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"MOCK98: cannot send", port:'MOCK98', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"MOCK99: incoming messages, and can send", port:'MOCK99', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"MOCK100: incoming messages for autoreplies", port:'MOCK100', baud:9600).save(failOnError:true)	
	}

	private def dev_initPolls() {
		if(!bootstrapData) return
		def keyword1 = new Keyword(value: 'FOOTBALL')
		def keyword2 = new Keyword(value: 'SOCCER')
		def keyword3 = new Keyword(value: 'MANU', isTopLevel:false, ownerDetail:'A')
		def keyword4 = new Keyword(value: 'BARCA', isTopLevel:false, ownerDetail:'B')
		def keyword5 = new Keyword(value: 'UTD', isTopLevel:false, ownerDetail:'A')
		def keyword6 = new Keyword(value: 'FCB', isTopLevel:false, ownerDetail:'B')
		def poll1 = new Poll(name: 'Football Teams', question:"Who will win?", sentMessageText:"Who will win? Reply FOOTBALL MANU for 'manchester' or FOOTBALL BARCA for 'barcelona'", autoreplyText:"Thank you for participating in the football poll")
		poll1.addToKeywords(keyword1).addToKeywords(keyword2).addToKeywords(keyword3).addToKeywords(keyword4).addToKeywords(keyword5).addToKeywords(keyword6)
		poll1.addToResponses(key:'A', value:'manchester')
		poll1.addToResponses(key:'B', value:'barcelona')
		poll1.addToResponses(PollResponse.createUnknown())
		
		def poll2 = new Poll(name: 'Shampoo Brands', question:"What shampoo brand do you prefer?", sentMessageText:"What shampoo brand do you prefer? Reply 'pantene' or 'oriele'")
		poll2.addToResponses(key: 'A', value: 'pantene')
		poll2.addToResponses(key: 'B', value: 'oriele')
		poll2.addToResponses(PollResponse.createUnknown())
		poll2.addToKeywords(value:'PANTENE', ownerDetail: 'A')
		poll2.addToKeywords(value:'ORIELE', ownerDetail: 'B')
		
		poll1.save(failOnError:true, flush:true)
		poll2.save(failOnError:true, flush: true)
		PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('+198765432'))
		PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('+123456789'))
		PollResponse.findByValue('pantene').addToMessages(TextMessage.findBySrc('Joe'))

		def barcelonaResponse = PollResponse.findByValue('barcelona');
		10.times {
			def msg = new TextMessage(src: "+9198765432${it}", date: new Date() - it, text: "Yes", inbound:true);
			msg.save(failOnError: true);
			barcelonaResponse.addToMessages(msg);
		}
		poll1.save(flush: true)
		poll2.save(flush: true)
	}
	
	private def dev_initAutoreplies() {
		if(!bootstrapData) return
		def k1 = new Keyword(value: "COLOR")
		def k2 = new Keyword(value: "AUTOREPLY")

		new Autoreply(name:"Toothpaste", autoreplyText: "Thanks for the input")
			.addToKeywords(value:"MENO")
			.addToKeywords(value:"TEETH")
			.save(failOnError:true, flush:true)
		new Autoreply(name:"Color", autoreplyText: "ahhhhhhhhh")
			.addToKeywords(value:"COLOUR")
			.addToKeywords(value:"COLOR")
			.save(failOnError:true, flush:true)
	}

	private def dev_initAutoforwards() {
		if(!bootstrapData) return
		new Autoforward(name:'Excitement', sentMessageText:'This is exciting: ${message_text}')
			.addToKeywords(value:'FORWARD')
			.addToSmartGroups(SmartGroup.findByName('Test Contacts'))
			.save(failOnError:true, flush:true)
	}
	
	private def dev_initFolders() {
		if(!bootstrapData) return
		['Work', 'Projects'].each {
			new Folder(name:it).save(failOnError:true, flush:true)
		}
		[new TextMessage(src:'Max', text:'I will be late'),
				new TextMessage(src:'Jane', text:'Meeting at 10 am'),
				new TextMessage(src:'Patrick', text:'Project has started'),
				new TextMessage(src:'Zeuss', text:'Sewage blocked')].each() {
			it.inbound = true
			it.date = new Date()
			it.save(failOnError:true, flush:true)
		}

		[Folder.findByName('Work').addToMessages(TextMessage.findBySrc('Max')),
				Folder.findByName('Work').addToMessages(TextMessage.findBySrc('Jane')),
				Folder.findByName('Projects').addToMessages(TextMessage.findBySrc('Zeuss')),
				Folder.findByName('Projects').addToMessages(TextMessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}

		def m = TextMessage.findByText("modem message")
		def modem = SmslibFconnection.list()[0]
		modem.addToMessages(m)
		m.connectionId = modem.id
		m.save(failOnError:true, flush:true)
	}
	
	private def dev_initAnnouncements() {
		if(!bootstrapData) return
		[new TextMessage(src:'Roy', text:'I will be late'),
			new TextMessage(src:'Marie', text:'Meeting at 10 am'),
			new TextMessage(src:'Mike', text:'Project has started')].each() {
				it.inbound = true
				it.date = new Date()
			it.save(failOnError:true, flush:true)
		}
		def a1 = new Announcement(name:'Free cars!', sentMessageText:"Everyone who recieves this message will also recieve a free Subaru")
		def a2 = new Announcement(name:'Office Party', sentMessageText:"Office Party on Friday!")
		def sent1 = new TextMessage(src:'me', inbound:false, text:"Everyone who recieves this message will also recieve a free Subaru")
		def sent2 = new TextMessage(src:'me', inbound:false, text:"Office Party on Friday!")
		sent1.addToDispatches(dst:'+254116633', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
		sent2.addToDispatches(dst:'+254116633', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
		a1.addToMessages(sent1).save(failOnError:true, flush:true)
		a2.addToMessages(sent2).save(failOnError:true, flush:true)
		
		[Announcement.findByName('Free cars!').addToMessages(TextMessage.findBySrc('Roy')),
				Announcement.findByName('Free cars!').addToMessages(TextMessage.findBySrc('Marie')),
				Announcement.findByName('Office Party').addToMessages(TextMessage.findBySrc('Mike'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	private def dev_initWebconnections() {
		if(!bootstrapData) return
		[	new TextMessage(src:'Wanyama', text:'forward me to the server'),
			new TextMessage(src:'Tshabalala', text:'a text from me'),
			new TextMessage(src:'June', text:'I just arrived'),
			new TextMessage(src:'Otieno', text:'I am on a map!'),
			new TextMessage(src:'Ekisa', text:'I too am on a map'),
			new TextMessage(src:'James', text:'I just arrived')].each() {
				it.inbound = true
				it.date = new Date()
			it.save(failOnError:true, flush:true)
		}

		def extCmd = new GenericWebconnection(name:'GET to Server', url:"http://192.168.0.200:9091/webservice-0.1/message/get", httpMethod:Webconnection.HttpMethod.GET, apiEnabled: true, secret: "shh")
			.addToKeywords(value:'WEBCONN')
			.addToKeywords(value:'UPLOAD')
		extCmd.addToRequestParameters(new RequestParameter(name:'text' , value: '${message_body}'))
		extCmd.addToRequestParameters(new RequestParameter(name:'text_with_keyword' , value: '${message_body_with_keyword}'))
		extCmd.addToRequestParameters(new RequestParameter(name:'date' , value: '${message_timestamp}'))
		extCmd.addToRequestParameters(new RequestParameter(name:'sender' , value: '${message_src_number}'))
		extCmd.addToRequestParameters(new RequestParameter(name:'senderName' , value: '${message_src_name}'))
		extCmd.save(failOnError:true, flush:true)
		def sent1 = new TextMessage(src:'me', inbound:false, text:"Your messages are in 'the cloud'")
		sent1.addToDispatches(dst:'+254116633', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
		extCmd.addToMessages(sent1).save(failOnError:true, flush:true)
		extCmd.addToMessages(TextMessage.findBySrc('Wanyama'))
		extCmd.addToMessages(TextMessage.findBySrc('Tshabalala'))
		extCmd.addToMessages(TextMessage.findBySrc('June'))
		extCmd.save(failOnError:true, flush:true)
		def extCmdPost = new GenericWebconnection(name:'POST to Server', url:"http://192.168.0.200:9091/webservice-0.1/message/post", httpMethod:Webconnection.HttpMethod.POST)
			.addToKeywords(value:'POST')
		extCmdPost.addToRequestParameters(new RequestParameter(name:'text' , value: '${message_body}'))
		extCmdPost.addToRequestParameters(new RequestParameter(name:'date' , value: '${message_timestamp}'))
		extCmdPost.addToRequestParameters(new RequestParameter(name:'sender' , value: '${message_src_number}'))
		extCmdPost.save(failOnError:true, flush:true)

		def ushahidiWebconnection = new UshahidiWebconnection(name:'Ushahidi', url:"http://192.168.0.200:80/ushahidi/frontlinesms/", httpMethod:Webconnection.HttpMethod.GET)
			.addToKeywords(value:"USHAHIDI")
			.addToKeywords(value:"MAP")
		ushahidiWebconnection.addToRequestParameters(new RequestParameter(name:'m' , value: '${message_body}'))
		ushahidiWebconnection.addToRequestParameters(new RequestParameter(name:'key' , value: '1NIJP34G'))
		ushahidiWebconnection.addToRequestParameters(new RequestParameter(name:'s' , value: '${message_src_number}'))
		ushahidiWebconnection.save(failOnError:true, flush:true)
		def ushSent = new TextMessage(src:'me', inbound:false, text:"Your messages are on Ushahidi!")
		ushSent.addToDispatches(dst:'+25411663123', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
		ushahidiWebconnection.addToMessages(ushSent).save(failOnError:true, flush:true)
		ushahidiWebconnection.addToMessages(TextMessage.findBySrc('Otieno'))
		ushahidiWebconnection.addToMessages(TextMessage.findBySrc('Ekisa'))
		ushahidiWebconnection.addToMessages(TextMessage.findBySrc('James'))
		ushahidiWebconnection.save(failOnError:true, flush:true)
	}

	private def dev_initSubscriptions() {
		if(!bootstrapData) return

		def allrounderBobby = new Contact(name:'Bobby', mobile:"987654321").save(failOnError:true)
		def camperSam = new Contact(name:"Sam", mobile:"987654322").save(failOnError:true)
		def footyRon = new Contact(name:'Ron', mobile:"987654323").save(failOnError:true)

		def campingGroup = new Group(name:"Camping Group").save(failOnError:true)
		def campingSub = new Subscription(name:"Camping Subscription", group:campingGroup, defaultAction:Subscription.Action.JOIN)
			.addToKeywords(value:"CAMPING", isTopLevel:true)
			.addToKeywords(value:"IN", isTopLevel:false, ownerDetail:"JOIN")
			.addToKeywords(value:"YES", isTopLevel:false, ownerDetail:"JOIN")
			.addToKeywords(value:"OUT", isTopLevel:false, ownerDetail:"LEAVE")
			.addToKeywords(value:"NO", isTopLevel:false, ownerDetail:"LEAVE")
			.save(failOnError:true)
		campingGroup.addToMembers(allrounderBobby)
		campingGroup.addToMembers(camperSam)

		campingGroup.save(failOnError:true)

		def footballGroup = new Group(name:"Football Updates").save(failOnError:true)
		def footballSub = new Subscription(name:"Football Updates Subscription", group:campingGroup, defaultAction:Subscription.Action.JOIN)
			.addToKeywords(value:"JOIN", isTopLevel:true, ownerDetail: "JOIN")
			.addToKeywords(value:"LEAVE", isTopLevel:true, ownerDetail: "LEAVE")
			.save(failOnError:true)
		
		footballGroup.addToMembers(allrounderBobby)
		footballGroup.addToMembers(footyRon)

		footballGroup.save(failOnError:true)
	}

	private def dev_initCustomActivities() {
		if(!bootstrapData) return

		def uploadStep = new WebconnectionActionStep()
			.setPropertyValue('url', 'http://frontlinesms.com')
			.setPropertyValue('httpMethod', 'GET')
			.setPropertyValue('myNumber', '23123123')
			.setPropertyValue('myMessage', 'i will upload forever')
		def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1"))
		def forwardStep = new ForwardActionStep()
				.addToStepProperties(new StepProperty(key:'sentMessageText',value:'sending forward ${message_text}'))
				.addToStepProperties(new StepProperty(key:'recipient',value:'Address-123123'))
		def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"2"))
		def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"I will send you forever"))

		new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToSteps(replyStep)
				.addToSteps(forwardStep)
				.addToSteps(uploadStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
	}
	
	private def dev_initLogEntries() {
		if(!bootstrapData) return

		def now = new Date()
		[new LogEntry(date:now, content: "entry1"),
				new LogEntry(date:now-2, content: "entry2"),
				new LogEntry(date:now-6, content: "entry3"),
				new LogEntry(date:now-13, content: "entry4"),
				new LogEntry(date:now-27, content: "entry5"),
				new LogEntry(date:now-100, content: "entry6")]*.save(failOnError:true, flush:true)
	}

	private def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}

	private def createContact(String n, String a) {
		def c = new Contact(name: n, mobile: a)
		c.save(failOnError: true)
	}

	private def initialiseSerial() {
		if(Environment.current == Environment.TEST
				|| Boolean.parseBoolean(System.properties['serial.mock'])) {
			initialiseMockSerial()
		} else {
			initialiseRealSerial()
		}

		def ports = serial.CommPortIdentifier.portIdentifiers
		if(ports) {
			println "PORTS:"
			ports.each {
				println "> Port identifier: ${it}"
			}
			println "END OF PORTS LIST"
		} else {
			println '''NO SERIAL PORTS DETECTED.  IF YOU ARE RUNNING *NIX, PLEASE CHECK THAT YOU
ARE A MEMBER OF THE APPROPRIATE GROUP (e.g. "dialout").  OTHERWISE MAKE SURE THAT
YOU HAVE A COMPATIBLE SERIAL LIBRARY INSTALLED.'''
		}
	}

	private def initialiseRealSerial() {
		dev_initRealSmslibFconnections()
		
		def jniPath = grailsApplication.parentContext.getResource("jni").file.absolutePath
		println "JNI Path: $jniPath"
		
		// set javax.comm.properties path (for Windows only)
		SerialClassFactory.javaxCommPropertiesPath = jniPath + "/windows/javax.comm.properties"
		
		// adapted from http://techdm.com/grails/?p=255&lang=en
		def addJavaLibraryPath = { path, boolean prioritise=true ->
			def dir = new File(path)
			println "Absolute location of JNI libraries: ${dir.absolutePath}"
			assert dir.exists()
			def oldPathList = System.getProperty('java.library.path', '')
			def newPathList = prioritise?
					dir.canonicalPath + File.pathSeparator + oldPathList:
					oldPathList + File.pathSeparator + dir.canonicalPath
			log.info "Setting java.library.path to $newPathList"
			System.setProperty('java.library.path', newPathList)
			ClassLoader.@sys_paths = null
		}
		
		def os = {
			def osNameString = System.properties['os.name'].toLowerCase()
			for(name in ['linux', 'windows', 'mac']) {
				if(osNameString.contains(name)) return name
			}
		}.call()

		/* Check whether architecture is 32- or 64-bit
		 * For OSX, we check the processor hardware. For other OSs, we use the os.arch jvm system property.
		 * Note that os.arch actually returns the JVM architecture, not the hardware arch.
		*/
		def osArch = os=='mac'?Runtime.runtime.exec('uname -m').text:System.properties['os.arch']
		def architecture = osArch=='amd64'?'amd64': osArch.contains('64')? 'x86_64': 'i686'
		
		log.info "Adding $jniPath/$os/$architecture to library paths..."
		addJavaLibraryPath "$jniPath/$os/$architecture"
		serial.SerialClassFactory.init(serial.SerialClassFactory.PACKAGE_RXTX) // TODO hoepfully this step of specifying the package is unnecessary
	}

	private def initialiseNonSmslibFconnections() {
		Fconnection.findAllByEnabled(true).each { connection ->
			if (connection.shortName != "smslib") {
				println "CoreBootStrap.initialiseNonSmslibFconnections() :: creating routes for $connection.shortName:$connection.id"
				fconnectionService.createRoutes(connection)
			}
		}
	}

	private def activateActivities() {
		Activity.findAllByArchivedAndDeleted(false, false).each { activity ->
			activity.activate()
		}
	}

	private def updateFeaturePropertyFileValues(){
		def currentVersion = ApplicationHolder.application.metadata.'app.version'
		def previousVersionRun = appSettingsService['version.lastrun']
		if(currentVersion != previousVersionRun) {
			appSettingsService['newfeatures.popup.show.infuture'] = true
			appSettingsService['version.lastrun'] = currentVersion
			appSettingsService.persist()
		}
		appSettingsService['newfeatures.popup.show.immediately'] = appSettingsService['newfeatures.popup.show.infuture']
	}

	private def initialiseMockSerial() {
		dev_initMockSmslibFconnections()
		
		MockModemUtils.initialiseMockSerial([
				MOCK95:new CommPortIdentifier("MOCK95", MockModemUtils.createMockPortHandler_rejectPin()),
				MOCK96:new CommPortIdentifier("MOCK96", MockModemUtils.createMockPortHandler_disconnectOnReceive()),
				MOCK97:new CommPortIdentifier("MOCK97", MockModemUtils.createMockPortHandler_badPort()),
				MOCK98:new CommPortIdentifier("MOCK98", MockModemUtils.createMockPortHandler_sendFails()),
				MOCK99:new CommPortIdentifier("MOCK99", MockModemUtils.createMockPortHandler_withMessages()),
				MOCK100:new CommPortIdentifier("MOCK100", MockModemUtils.createMockPortHandler_withTextMessages(dev_initMockPortMessages()))])
	}
	
	private def dev_initMockPortMessages() {
		return ["AUTOREPLY", "autorely", "auToreply", "colorz", "color z", ""];
	}

	private def dev_disableSecurityFilter() {
		appSettingsService.set("auth.basic.enabled", '')
		appSettingsService.set("auth.basic.username", '')
		appSettingsService.set("auth.basic.password", '')
	}

	private Date createDate(String dateAsString) {
		DateFormat format = createDateFormat()
		return format.parse(dateAsString)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}

	private def ensureResourceDirExists() {
		def dir = new File(ResourceUtils.getResourcePath())
		if (!dir.exists()) {
			dir.mkdirs()
			log.info "creating resource directory at {$dir.absolutePath}"
		}
	}

	private def setCustomJSONRenderers() {
		JSON.registerObjectMarshaller(Announcement) {
			[id:it.id, dateCreated:it.dateCreated, name:it.name, sentMessageText:it.sentMessageText]
		}
		JSON.registerObjectMarshaller(DetectedDevice) {
			[port:it.port, description:it.description, lockType:it.lockType]
		}
	}

	private setDefaultMessageRoutingPreferences(){
		if(!appSettingsService.get('routing.preferences.edited') || (appSettingsService.get('routing.preferences.edited') == false)){
			println "### Changing Routing preferences ###"
			appSettingsService.set('routing.uselastreceiver', false)
			appSettingsService.set('routing.preferences.edited', true)
		}
		else {
			def fconnectionInstanceList = Fconnection.findAllBySendEnabled(true)
			def fconnectionIdList = fconnectionInstanceList.collect {"fconnection-${it.id}"}.join(",")
			appSettingsService.set('routing.use', fconnectionIdList)
		}
	}

	private def updateAvailableFconnections() {
		println "# CoreBootStrap.updateAvailableFconnections() :: Fconnection implementations before pruning: ${Fconnection.implementations}"
		Fconnection.implementations.remove(ClickatellFconnection)
		Fconnection.implementations.remove(IntelliSmsFconnection)
		Fconnection.implementations.remove(NexmoFconnection)
		println "# CoreBootStrap.updateAvailableFconnections() :: Fconnection implementations after pruning: ${Fconnection.implementations}"
	}
}

