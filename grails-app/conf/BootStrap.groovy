import grails.util.Environment
import frontlinesms2.*
import org.mockito.Mockito
import java.lang.reflect.Field
import serial.SerialClassFactory
import serial.mock.MockSerial
import serial.mock.SerialPortHandler
import serial.mock.CommPortIdentifier
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import net.frontlinesms.test.serial.hayes.*
import frontlinesms2.dev.MockModemUtils

class BootStrap {
	def grailsApplication
	
	def init = { servletContext ->
		initialiseSerial()
		addTruncateMethodToStrings()
		
		switch(Environment.current) {
			case Environment.TEST:
				test_initGeb()
				break
				
			case Environment.DEVELOPMENT:
				//DB Viewer
				//org.hsqldb.util.DatabaseManager.main()
				// do custom init for dev here
				dev_initSmartGroups()
				dev_initGroups()
				dev_initContacts()
				dev_initFconnections()
				dev_initFmessages()
				dev_initPolls()
				dev_initFolders()
				dev_initAnnouncements()
				dev_initRadioShows()
				break
		}
	}

	def destroy = {
	}
	
	private def addTruncateMethodToStrings() {
		String.metaClass.truncate = { max=16 ->
		    delegate.size() <= max? delegate: delegate.substring(0, max-1) + '…'
		}
	}

	/** Initialise SmartGroup domain objects for development and demos. */
	private def dev_initContacts() {
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
			new Contact(name:"test-${it}", primaryMobile:"number-${it}").save(failOnError:true)
		}
		
		[new CustomField(name: 'lake', value: 'Victoria', contact: alice),
				new CustomField(name: 'town', value: 'Kusumu', contact: bob)].each() {
			it.save(failOnError:true, flush:true)
		}
	}
	
	private def dev_initSmartGroups() {
		new SmartGroup(name:'Kenyans', mobile:'+254').save(failOnError:true)
		new SmartGroup(name:'Test Contacts', contactName:'test-').save(failOnError:true)
	}
	
	private def dev_initGroups() {
		['Friends', 'Listeners', 'Not Cats', 'Adults'].each() { createGroup(it) }
	}
	
	private def dev_initFmessages() {
		new Fmessage(src:'+123987123', dst:'+44123456789',
				text:'A really long message which should be beautifully truncated so we can all see what happens in the UI when truncation is required.',
				status:MessageStatus.INBOUND).save(failOnError:true)
		
		[new Fmessage(src:'+123456789', dst:'+2541234567', text:'manchester rules!'),
				new Fmessage(src:'+198765432', dst:'+254987654', text:'go manchester'),
				new Fmessage(src:'Joe', dst:'+254112233', text:'pantene is the best', dateReceived:new Date()-1),
				new Fmessage(src:'Jill', dst:'+254987654', text:"where's the hill?", dateReceived:createDate("2011/01/21")),
				new Fmessage(src:'+254675334', dst:'+254112233', text:"where's the pale?", dateReceived:createDate("2011/01/20")),
				new Fmessage(src:'Humpty', dst:'+254112233', text:"where're the king's men?", starred:true, dateReceived:createDate("2011/01/23"))].each() {
			it.status = MessageStatus.INBOUND
			it.save(failOnError:true)
		}
		(1..101).each {
			new Fmessage(src:'+198765432', dst:'+254987654', text:"text-${it}", dateReceived: new Date() - it, status:MessageStatus.INBOUND).save(failOnError:true)
		}

		[new Fmessage(src: '+3245678', dst: '+123456789', text: "time over?", status: MessageStatus.SEND_FAILED),
				new Fmessage(src: 'Johnny', dst: '+254114433', text: "I am in a meeting", status: MessageStatus.SENT),
				new Fmessage(src: 'Sony', dst: '+254116633', text: "Hurry up", status: MessageStatus.SENT),
				new Fmessage(src: 'Jill', dst: '+254115533', text: "sample sms", status: MessageStatus.SEND_PENDING)].each {
			it.save(failOnError: true)
		}
	}
	
	private def dev_initFconnections() {
		new EmailFconnection(name:"mr testy's email", receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
				serverPort:993, username:'mr.testy@zoho.com', password:'mister').save(failOnError:true)

		new SmslibFconnection(name:"Huawei Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234').save(failOnError:true)
		new SmslibFconnection(name:"COM4", port:'COM4', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"USB0", port:'/dev/ttyUSB0', baud:9600, pin:'1149').save(failOnError:true)

		new SmslibFconnection(name:"COM98 mock smslib device", port:'COM98', baud:9600).save(failOnError:true)
		new SmslibFconnection(name:"COM99 mock smslib device", port:'COM99', baud:9600).save(failOnError:true)
	}
	
	private def dev_initPolls() {
		[Poll.createPoll(title: 'Football Teams', keyword:'football', choiceA: 'manchester', choiceB:'barcelona', message:'who will win?', messageText:"Who will win? Reply FOOTBALL A for 'manchester' or FOOTBALL B for 'barcelona'", autoReplyText:"Thank you for participating in the football poll"),
				Poll.createPoll(title: 'Shampoo Brands', choiceA: 'pantene', choiceB:'oriele', messageText:"What shampoo brand do you prefer? Reply 'pantene' or 'oriele'")].each() {
			it.save(failOnError:true, flush:true)
		}

		PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('+198765432'))
		PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('+123456789'))
		PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))

		def barcelonaResponse = PollResponse.findByValue('barcelona');
		10.times {
			def msg = new Fmessage(src: "+9198765432${it}", dst: "+4498765432${it}",dateReceived: new Date() - it, text: "Yes", status: MessageStatus.INBOUND);
			msg.save(failOnError: true);
			barcelonaResponse.addToMessages(msg);
		}
	}
	
	private def dev_initFolders() {
		['Work', 'Projects'].each {
			new Folder(name:it).save(failOnError:true, flush:true)
		}

		[new Fmessage(src:'Max', dst:'+254987654', text:'I will be late'),
				new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am'),
				new Fmessage(src:'Patrick', dst:'+254112233', text:'Project has started'),
				new Fmessage(src:'Zeuss', dst:'+234234', text:'Sewage blocked')].each() {
			it.status = MessageStatus.INBOUND
			it.dateReceived = new Date()
			it.save(failOnError:true, flush:true)
		}

		[Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Max')),
				Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Jane')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Zeuss')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}
	
	private def dev_initAnnouncements() {
		new Announcement(name: 'Free cars!', sentMessage:"Everyone who recieves this message will also recieve a free Subaru").save(failOnError:true, flush:true)
		new Announcement(name: 'Office Party', sentMessage:"Office Party on Friday!").save(failOnError:true, flush:true)
		
		[Announcement.findByName('Free cars!').addToMessages(Fmessage.findBySrc('Roy')),
				Announcement.findByName('Free cars!').addToMessages(Fmessage.findBySrc('Marie')),
				Announcement.findByName('Office Party').addToMessages(Fmessage.findBySrc('Mike'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}
	
	private def dev_initRadioShows() {
		def r = new RadioShow(name: "Health")
		r.addToMessages(new Fmessage(text: "eat fruits", src: "src", dst: "dst"))
		r.addToMessages(new Fmessage(text: "excerise", src: "src", dst: "dst"))
		r.save(failOnError: true, flush: true)
	}

	private def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}

	private def createContact(String n, String a) {
		def c = new Contact(name: n, primaryMobile: a)
		c.save(failOnError: true)
	}
	
	private def initialiseSerial() {
		if(Environment.current == Environment.TEST
				|| Boolean.parseBoolean(System.properties['serial.mock']))
			initialiseMockSerial()
		else
			initialiseRealSerial()

		println "PORTS:"
		serial.CommPortIdentifier.portIdentifiers.each {
			println "> Port identifier: ${it}"
		}
		println "END OF PORTS LIST"
	}
	
	private def initialiseRealSerial() {
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

		def osArch = System.properties['os.arch']
		def architecture = osArch=='amd64'?'amd64': osArch.contains('64')? 'x86_64': 'i686'
		
		log.info "Adding $jniPath/$os/$architecture to library paths..."
		addJavaLibraryPath "$jniPath/$os/$architecture"
		serial.SerialClassFactory.init(serial.SerialClassFactory.PACKAGE_RXTX) // TODO hoepfully this step of specifying the package is unnecessary
	}

	private def initialiseMockSerial() {
		CommPortIdentifier cpi = new CommPortIdentifier("COM99", MockModemUtils.createMockPortHandler())
		MockModemUtils.initialiseMockSerial([COM98:cpi, COM99:cpi])
	}
	
	private def test_initGeb() {
		def emptyMc = new geb.navigator.AttributeAccessingMetaClass(new ExpandoMetaClass(geb.navigator.EmptyNavigator))
		def nonEmptyMc = new geb.navigator.AttributeAccessingMetaClass(new ExpandoMetaClass(geb.navigator.NonEmptyNavigator))
		
		/** list of boolean vars from https://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebElement.html#getAttribute(java.lang.String) */
		final def BOOLEAN_PROPERTIES = ['async', 'autofocus', 'autoplay', 'checked', 'compact', 'complete', 'controls', 'declare', 'defaultchecked', 'defaultselected', 'defer', 'disabled', 'draggable', 'ended', 'formnovalidate', 'hidden', 'indeterminate', 'iscontenteditable', 'ismap', 'itemscope', 'loop', 'multiple', 'muted', 'nohref', 'noresize', 'noshade', 'novalidate', 'nowrap', 'open', 'paused', 'pubdate', 'readonly', 'required', 'reversed', 'scoped', 'seamless', 'seeking', 'selected', 'spellcheck', 'truespeed', 'willvalidate']
		BOOLEAN_PROPERTIES.each { name ->
			def getterName = "is${name.capitalize()}"
			emptyMc."$getterName" = { false }
			nonEmptyMc."$getterName" = {
				def v = delegate.getAttribute(name)
				!(v == null || v.length()==0 || v.equalsIgnoreCase('false'))
			}
		}
		emptyMc.initialize()
		geb.navigator.EmptyNavigator.metaClass = emptyMc
		nonEmptyMc.initialize()
		geb.navigator.NonEmptyNavigator.metaClass = nonEmptyMc
	}

	private Date createDate(String dateAsString) {
		DateFormat format = createDateFormat();
		return format.parse(dateAsString)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd")
	}
}
