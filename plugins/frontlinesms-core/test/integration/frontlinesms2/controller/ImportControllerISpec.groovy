package frontlinesms2.controller

import frontlinesms2.*
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile

class ImportControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final String FILE_TYPE_CSV = ''
	private static final String FILE_TYPE_VCF = 'text/vcard'


	def controller
	
	def setup() {
		controller = new ImportController()
	}

	def 'Uploading a contacts CSV file should create new contacts and groups in the database'() {
		when:
			importContactCsv('''"Name","Mobile Number","Other Mobile Number","Email","Current Status","Notes","Group(s)"
"Alice Sihoho","+254728749000","","","true","","/ToDo/Work"
"Amira Cheserem","+254715840801","","","true","","/ToDo/Work"
"anyango Gitu","+254727689908","","","true","","/isIt\\\\/ToDo/Work/jobo"
''')
		then:
			// check that contacts and groups were created
			waitAbit { Contact.list()*.name.sort() == ['Alice Sihoho', 'Amira Cheserem', 'anyango Gitu'] }
			waitAbit { Group.list()*.name.sort() == ['ToDo', 'ToDo-Work', 'ToDo-Work-jobo', 'Work', 'isIt', 'jobo'] }
	}
	
	def 'uploading contacts with backslash characters should unfortunately interpret them as separate groups'() {
		when:
			importContactCsv('''"Name","Mobile Number","Email","Notes","Group(s)","lake","town"
"Alex","0702597711",,,"\\o/ team",,
"Enock","0711756950",,,"\\o/ team",,
"Geoff","0725675317",,,"\\o/ team",,
"Vaneyck","0723127992",,,"\\o/ team",,
''')
		then:
			waitAbit { Group.list()*.name == ['o', 'o- team', 'team'] }
			waitAbit { Contact.count() == 4 }
			def groups = Group.findAll()
			waitAbit { Contact.findAll().every { groups.every { group -> it.isMemberOf(group) } } }
	}
	
	def 'Uploading a messages CSV file from version 1 should create new messages and folder in the database'() {
		when:
			importMessages('''"Message Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","Message Received Msg1.","Safaricom","254704593656"
"Received","Received","2012-02-24 17:22:59","Message Received Msg2.","254705693656","254704593656"
"Sent","Failed","2012-03-09 12:48:42","Message Sent Msg1","* N/A *","0720330266"
"Sent","Sent","2012-03-07 12:19:41","Message Sent Msg2","* N/A *","144"
''')
		then:
			// check that messages and folders were created
			waitAbit { TextMessage.list()*.text.sort() == ['Message Received Msg1.', 'Message Received Msg2.', 'Message Sent Msg1', 'Message Sent Msg2'] }
			waitAbit { Folder.list().name == ['messages from v1'] }
			waitAbit { TextMessage.list()*.messageOwner.name.every { it == 'messages from v1' } }
	}
	
	def 'Uploading a messages CSV file from version 2 should create new messages and folder in the database'() {
		when:
			importMessages('''"DatabaseID","Source Name","Source Mobile","Destination Name","Destination Mobile","Text","Date Created"
"302","Simon","+123987123","","[]","Message 1","2012-07-27 10:22:02.943"
"302","Says","+123987123","","[]","Message 2","2012-07-27 10:22:02.943"
"302","Import","+123987123","","[]","Message 3","2012-07-27 10:22:02.943"
''')
		then:
			// check that messages and folders were created
			waitAbit { TextMessage.list()*.text.sort() == ['Message 1', 'Message 2', 'Message 3'] }
			waitAbit { Folder.list().name == ['messages from v2'] }
			waitAbit { TextMessage.list()*.messageOwner.name.every { it == 'messages from v2' } }
	}

	def 'Uploading a messages CSV file from version 2 should be able to handle line breaks in messages'() {
		when:
			importMessages('''"DatabaseID","Source Name","Source Mobile","Destination Name","Destination Mobile","Text","Date Created"
"27",,+12345678,"[Bobby Briggs]","[+2547123456]","Joyce
Vancouver
Siloi
Rotation
Amelia
Georgina
Shantelle","2012-06-12 15:58:44.488"
''')
		then:
			// check that messages and folders were created
			waitAbit { TextMessage.list()*.text.sort() == ['''Joyce
Vancouver
Siloi
Rotation
Amelia
Georgina
Shantelle'''] }
			waitAbit { Folder.list().name == ['messages from v2'] }
			waitAbit { TextMessage.list()*.messageOwner.name.every { it == 'messages from v2' } }
	}

	def 'Uploading a message with a very long content field results in the message content being...'() {
		when:
			importMessages('''"Message Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","''' + ('0123456789ABCDEF' * 256)  + '''","Safaricom","254704593656"
"Received","Received","2012-02-24 17:22:59","short message","254705693656","254704593656"
''')
		then:
			waitAbit { TextMessage.list()*.text == ['short message', ('0123456789ABCDEF' * 256)[0..1598] + '…'] }
	}

	def 'Uploading a CSV with a BOM should not cause issues'() {
		when:
			importMessages('''"\uFEFFMessage Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","+123456789","Safaricom","254704593656"
''')
		then:
			waitAbit { TextMessage.list()*.inbound == [true] }
	}

	def 'contact import should support vcard 2.1'() {
		when:
			importVcard('''BEGIN:VCARD
VERSION:2.1
N:Gump;Forrest
FN:Forrest Gump
ORG:Bubba Gump Shrimp Co.
TITLE:Shrimp Man
PHOTO;GIF:http://www.example.com/dir_photos/my_photo.gif
TEL;WORK;VOICE:(111) 555-1212
TEL;HOME;VOICE:(404) 555-1212
ADR;WORK:;;100 Waters Edge;Baytown;LA;30314;United States of America
LABEL;WORK;ENCODING=QUOTED-PRINTABLE:100 Waters Edge=0D=0ABaytown, LA 30314=0D=0AUnited States of America
ADR;HOME:;;42 Plantation St.;Baytown;LA;30314;United States of America
LABEL;HOME;ENCODING=QUOTED-PRINTABLE:42 Plantation St.=0D=0ABaytown, LA 30314=0D=0AUnited States of America
EMAIL;PREF;INTERNET:forrestgump@example.com
REV:20080424T195243Z
END:VCARD''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '1115551212', 'forrestgump@example.com']] }
	}

	def 'contact import should support vcard 3.0'() {
		when:
			importVcard('''BEGIN:VCARD
VERSION:3.0
N:Gump;Forrest;Mr.
FN:Forrest Gump
ORG:Bubba Gump Shrimp Co.
TITLE:Shrimp Man
PHOTO;VALUE=URL;TYPE=GIF:http://www.example.com/dir_photos/my_photo.gif
TEL;TYPE=WORK,VOICE:(111) 555-1212
TEL;TYPE=HOME,VOICE:(404) 555-1212
ADR;TYPE=WORK:;;100 Waters Edge;Baytown;LA;30314;United States of America
LABEL;TYPE=WORK:100 Waters Edge\nBaytown, LA 30314\nUnited States of America
ADR;TYPE=HOME:;;42 Plantation St.;Baytown;LA;30314;United States of America
LABEL;TYPE=HOME:42 Plantation St.\nBaytown, LA 30314\nUnited States of America
EMAIL;TYPE=PREF,INTERNET:forrestgump@example.com
REV:2008-04-24T19:52:43Z
END:VCARD''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '1115551212', 'forrestgump@example.com']] }
	}

	def 'contact import should support vcard 4.0'() {
		when:
			importVcard('''BEGIN:VCARD
VERSION:4.0
N:Gump;Forrest;;;
FN:Forrest Gump
ORG:Bubba Gump Shrimp Co.
TITLE:Shrimp Man
PHOTO;MEDIATYPE=image/gif:http://www.example.com/dir_photos/my_photo.gif
TEL;TYPE=work,voice;VALUE=uri:tel:+1-111-555-1212
TEL;TYPE=home,voice;VALUE=uri:tel:+1-404-555-1212
ADR;TYPE=work;LABEL="100 Waters Edge\nBaytown, LA 30314\nUnited States of America"
:;;100 Waters Edge;Baytown;LA;30314;United States of America
ADR;TYPE=home;LABEL="42 Plantation St.\nBaytown, LA 30314\nUnited States of America"
:;;42 Plantation St.;Baytown;LA;30314;United States of America
EMAIL:forrestgump@example.com
REV:20080424T195243Z
END:VCARD''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '+11115551212', 'forrestgump@example.com']] }
	}

	def 'contact import should support xCard'() {
		when:
			importVcard('''<?xml version="1.0" encoding="UTF-8"?>
<vcards xmlns="urn:ietf:params:xml:ns:vcard-4.0">
  <vcard>
    <n>
      <surname>Gump</surname><ref>{{cite web|last=Perreault|first=Simon|url=http://tools.ietf.org/html/rfc6351|publisher=Internet Engineering Task Force (IETF)|accessdate=18 September 2013}}</ref> 
      <given>Forrest</given>
    </n>
    <fn><text>Forrest Gump</text></fn>
    <title><text>Shrimp Man</text></title>
    <photo>
        <parameters>
            <mediatype><text>image/gif</text></mediatype>
        </parameters>
        <uri>http://www.example.com/dir_photos/my_photo.gif</uri>
    </photo>
    <tel>
      <parameters>
        <type>
          <text>work</text>
          <text>voice</text>
        </type>
      </parameters>
      <uri>tel:+1-111-555-1212</uri>
    </tel>
    <tel>
      <parameters>
        <type>
          <text>home</text>
          <text>voice</text>
        </type>
      </parameters>
      <uri>tel:+1-404-555-1212</uri>
    </tel>
    <adr>
      <parameters>
        <type><text>work</text></type>
        <label><text>100 Waters Edge
Baytown, LA 30314
United States of America</text></label>
      </parameters>
      <pobox/>
      <ext/>
      <street>100 Waters Edge</street>
      <locality>Baytown</locality>
      <region>LA</region>
      <code>30314</code>
      <country>United States of America</country>
    </adr>
    <adr>
      <parameters>
        <type><text>home</text></type>
        <label><text>100 Waters Edge
Baytown, LA 30314
United States of America</text></label>
      </parameters>
      <pobox/>
      <ext/>
      <street>42 Plantation St.</street>
      <locality>Baytown</locality>
      <region>LA</region>
      <code>30314</code>
      <country>United States of America</country>
    </adr>
    <email><text>forrestgump@example.com</text></email>
    <rev><timestamp>20080424T195243Z</timestamp></rev>
  </vcard>
</vcards>''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '+11115551212', 'forrestgump@example.com']] }
	}

	def 'contact import should support jCard'() {
		when:
			importVcard('''["vcardstream",
  ["vcard",
    [
      ["version", {}, "text", "4.0"],
      ["n", {}, "text", ["Gump", "Forrest", "", "", ""]],
      ["fn", {}, "text", "Forrest Gump"],
      ["org", {}, "text", "Bubba Gump Shrimp Co"],
      ["title", {} ,"text", "Shrimp Man"],
      ["photo", {"mediatype":"image/gif"}, "uri", "http://www.example.com/dir_photos/my_photo.gif"],
      ["tel", {"type":["work", "voice"]}, "uri", "tel:+1-111-555-1212"],
      ["tel", {"type":["home", "voice"]}, "uri", "tel:+1-404-555-1212"],
      ["adr",
        {"label":"100 Waters Edge\\nBaytown, LA 30314\\nUnited States of America", "type":"work"},
        "text",
        ["", "", "100 Waters Edge", "Baytown", "LA", "30314", "United States of America"]
      ],
      ["adr",
        {"label":"42 Plantation St.\\nBaytown, LA 30314\\nUnited States of America", "type":"home"},
        "text",
        ["", "", "42 Plantation St.", "Baytown", "LA", "30314", "United States of America"]
      ],
      ["email", {}, "text", "forrestgump@example.com"],
      ["rev", {}, "timestamp", "2008-04-24T19:52:43Z"]
    ]
  ]
]''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '+11115551212', 'forrestgump@example.com']] }
	}

	def 'contact import should support hCard'() {
		when:
			importVcard('''<html>
  <head>
    <link rel="profile" href="http://microformats.org/profile/hcard" />
  </head>
  <body>
    <div class="vcard">
      <img class="photo" src="http://www.example.com/dir_photos/my_photo.gif" align="left" />
      <h1 class="fn">Forrest Gump</h1>
      <div class="email">
        <span class="type">Internet</span> Email (<span class="type">pref</span>erred):
        <a class="value" href="mailto:forrestgump@example.com">forrestgump@example.com</a>
      </div>
      <div class="n">
        First Name: <span class="given-name">Forrest</span><br>
        Last Name: <span class="family-name">Gump</span>
      </div>
 
      <div class="label" style="display:none">
        <span class="type">home</span>
        42 Plantation St.<br>Baytown, LA 30314<br>United States of America
      </div>
      <div class="adr">
        <span class="type">Home</span> Address:<br>
        <span class="street-address">42 Plantation St.</span><br>
        <span class="locality">Baytown</span>, <span class="region">LA</span>
        <span class="postal-code">30314</span><br>
        <span class="country-name">United States of America</span>
      </div>
      <div class="tel">
         <abbr class="type" title="voice"></abbr>
         <span class="type">Home</span> Phone: <span class="value">+1-111-555-1212</span>
      </div>
 
      <div>
        Organization: <span class="org">Bubba Gump Shrimp Co.</span><br>
        Title: <span class="title">Shrimp Man</span>
      </div>
 
      <div class="label" style="display:none">
        <span class="type">work</span>
        100 Waters Edge<br>Baytown, LA 30314<br>United States of America
      </div>
      <div class="adr">
        <span class="type">Work</span> Address:<br>
        <span class="street-address">100 Waters Edge</span><br>
        <span class="locality">Baytown</span>, <span class="region">LA</span>
        <span class="postal-code">30314</span><br>
        <span class="country-name">United States of America</span>
      </div>
      <div class="tel">
         <abbr class="type" title="voice"></abbr>
         <span class="type">Work</span> Phone: <span class="value">+1-404-555-1212</span>
      </div>
 
      <em>vCard last updated:</em>
      <time class="rev" datetime="2008-04-24T19:52:43Z">April 24, 2008 at 7:52 PM GMT</time>
    </div>
  </body>
</html>''')
		then:
			waitAbit { Contact.list().size() == 1 }
			waitAbit { Contact.list().collect() {
				[it.name, it.mobile, it.email] } == [['Forrest Gump', '+11115551212', 'forrestgump@example.com']] }
	}

	def importMessages(String fileContent) {
		mockFileUpload('contactImportFile', fileContent)
		controller.importMessages()
	}

	def importContactCsv(String fileContent) {
		controller.params.csv = fileContent
		controller.params.reviewDone = true
		importContacts(fileContent)
	}

	def importVcard(String fileContent) {
		importContacts(fileContent, FILE_TYPE_VCF)
	}

	def importContacts(String fileContent, contentType=FILE_TYPE_CSV) {
		mockFileUpload('contactImportFile', fileContent, contentType)
		controller.importContacts()
	}

	def mockFileUpload(filename, fileContent, contentType=FILE_TYPE_CSV) {
		controller.request.addFile(new GrailsMockMultipartFile(filename, 'somefile', contentType, fileContent.getBytes("UTF-8")))
	}

	def waitAbit = { condition ->
		def conditionSatisfied = false
		for(x in (0..10)){
			if(condition) {
				conditionSatisfied = true
				break
			}
			sleep(500)
		}
		return conditionSatisfied
	}
}

