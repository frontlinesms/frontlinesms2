import groovy.io.FileType

String markdownLinkMatcher = /\[\d+\]: (\S+(?:\/\S+)*)/
File helpDir = new File("web-app", "help")
File imageDir = new File("web-app", "images/help")
File indexFile = new File("grails-app/views/help", "_index.gsp")

def validLinkTargets = {
	def links = []
	helpDir.eachFileRecurse(FileType.FILES) { f ->
		links << f.path - '.txt' - 'web-app/help/'
	}
	return links.sort().unique()
}.call()

def validImageTargets = {
	def images = []
	imageDir.eachFileRecurse(FileType.FILES) { f ->
		images << f.path - 'web-app/images/help/'
	}
	return images.sort().unique()
}.call()

def requestedLinksFromIndex = {
	def links = []
	indexFile.eachLine { line ->
		if(line ==~ markdownLinkMatcher) {
			links << (line =~ markdownLinkMatcher)[0][1]
		}
	}
	return links.sort().unique()
}.call()

def requestedLinksInFiles = {
	def links = []
	def images = []
	helpDir.eachFileRecurse(FileType.FILES) { f ->
		f.eachLine { line ->
			if(line ==~ markdownLinkMatcher) {
				def match = (line =~ markdownLinkMatcher)[0][1]
				if(match.startsWith('../images/help/')) {
					images << match - '../images/help/'
				} else {
					links << match
				}
			}
		}
	}
	return [links:links.sort().unique(), images:images.sort().unique()]
}.call()

def usedLinks = (requestedLinksFromIndex + requestedLinksInFiles.links).unique().findAll { !it.startsWith('http://') }
def brokenLinks = [links:usedLinks - validLinkTargets,
		images:requestedLinksInFiles.images - validImageTargets]
def unused = [links:validLinkTargets - usedLinks,
		images:validImageTargets - requestedLinksInFiles.images]

println "----------"
println "Missing Images: ${brokenLinks.images.size()}"
brokenLinks.images.each { println "\t$it" }
println "----------"
println "Unused Images: ${unused.images.size()}"
unused.images.each { println "\t$it" }
println "----------"
println "Broken links: ${brokenLinks.links.size()}"
brokenLinks.links.each { println "\t$it" }
println "----------"
println "Unused links: ${unused.links.size()}"
unused.links.each { println "\t$it" }
println "----------"

