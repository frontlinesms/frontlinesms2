package frontlinesms2
import java.io.File;
import java.io.InputStream;
import java.io.Reader;

class ImportController {
	def homeDir = "${System.properties.'user.home'}/.frontlinesms2/"
	def userDir = new File(homeDir, "/importedcsvfiles")
	
	def uploadCSVFile() {
		def fileextension = "" 
		def uploadedFile = request.getFile('importedcsvfile')
		if ( !uploadedFile.empty ) {
			fileextension = uploadedFile.originalFilename
			def splitArray = fileextension.split("\\.")
			fileextension = splitArray[splitArray.size()-1]
			
			if ( fileextension!="csv" ) {
				flash.message = "Please upload a csv file."
				redirect (controller: "settings", action: 'general')
				return ""
			}
			userDir.mkdirs()
			uploadedFile.transferTo( new File( userDir, uploadedFile.originalFilename))
			return "${homeDir}importedcsvfiles/${uploadedFile.originalFilename}"
		}
		return ""
	}

	def importedContacts = {
		def headerList = []
		def x = 0
		def savedCount = 0
		def failedCount = 0
		def uploadedCSVFile = ""
		uploadedCSVFile = uploadCSVFile()

		if ( uploadedCSVFile != "" ) {
			new File(uploadedCSVFile).eachCsvLine { tokens ->
				def y = 0
				def contactName = ""
				def mobileNo = ""
				def otherPhoneNo = ""
				def email = ""
				def notes = ""
				def groups = ""
				def customField = [:]
				def importedGroup = []
			
				if ( x!=0 ) {
					tokens.each {							
						if ( headerList[y].trim()!="Name" && headerList[y].trim()!="Mobile Number" && headerList[y].trim()!="Other Mobile Number" && headerList[y].trim()!="E-mail Address" && headerList[y].trim()!="Notes" && headerList[y].trim()!="Group(s)") {
							customField.put(headerList[y].trim(), tokens[y].trim())
						} else {
							if (headerList[y].trim()=="Group(s)") {
								def nestedgrp = tokens[y].trim()
								def spliednestedgrp =  nestedgrp.split("/")
								spliednestedgrp.each { if (it!="") importedGroup.add(it) }					
							} else {
								if ( headerList[y].trim()=="Name" ) {
									contactName = tokens[y].trim()
								} else if ( headerList[y].trim()=="Mobile Number" ) {
									mobileNo = tokens[y].trim()
								} else if ( headerList[y].trim()=="Other Mobile Number" ) {
									otherPhoneNo = tokens[y].trim()
								} else if ( headerList[y].trim()=="E-mail Address" ) {
									email = tokens[y].trim()
								} else if ( headerList[y].trim()=="Notes" ) {
									notes = tokens[y].trim()
								} 
							}
						}
						y++
						if (y==tokens.length) {
							def contact = new Contact(name: contactName, primaryMobile: mobileNo, secondaryMobile: otherPhoneNo, email: email, notes: notes)
							if (contact.save()) {
								savedCount++ 
								customField.eachWithIndex {
									new CustomField(name: it.key, value: customField.get(it.key), contact: contact).save()
								}
								def longGroup = ""
								importedGroup.each {
									def flsmsGroup = Group.findByName(it)
									if ( longGroup == "" ) { longGroup=it } else { longGroup=longGroup+ "-" +it }
									
									if ( flsmsGroup!=null ) { contact.addToGroups(flsmsGroup)
									} else {
										createGroup(it)
										contact.addToGroups(Group.findByName(it))
									}
									
									if (longGroup!=it && longGroup!="") {
										def flsmsGroupLong = Group.findByName(longGroup)
										if (flsmsGroupLong!=null) {
											contact.addToGroups(flsmsGroupLong)
										} else {
											createGroup(longGroup)
											contact.addToGroups(Group.findByName(longGroup))
										}
									}
								}
							} else {
								failedCount ++
							}
						}
					}
				} else {
					headerList = tokens
				}		
				x++
			}
			userDir.deleteDir()
			flash.message = "$savedCount contacts were imported; $failedCount failed" 
			redirect (controller: "settings", action: 'general')
		} else {
 			flash.message = "Invalid file."
			redirect (controller: "settings", action: 'general')
		}
	}
	
	private def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}
	
	private def createC(String n) {
		new Group(name: n).save(failOnError: true)
	}
}