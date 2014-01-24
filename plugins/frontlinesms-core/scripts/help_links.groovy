import groovy.io.FileType

String markdownLinkMatcher = /\[(\d+)\]: (\S+(?:\/\S+)*)/
// FIXME this doesn't work right now for matching link references inline if there are multiple links on the same line in inline text.
String inlineLinkMatcher = /.*\[(\d+)\].*/
File helpDir = new File("grails-app", "conf/help")
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
			links << (line =~ markdownLinkMatcher)[0][2]
		}
	}
	return links.sort().unique()
}.call()

def requestedLinksInFiles = {
	def links = []
	def images = []
	def listedIndexes = [:].withDefault { [] }
	def inlineIndexes = [:].withDefault { [] }
	helpDir.eachFileRecurse(FileType.FILES) { f ->
		if(!f.name.endsWith(".txt")) return;
		f.eachLine { line ->
			if(line ==~ markdownLinkMatcher) {
				def matcher = (line =~ markdownLinkMatcher)
				listedIndexes[f.path] << matcher[0][1]
				def match = matcher[0][2]
				if(match.startsWith('../images/help/')) {
					images << match - '../images/help/'
				} else {
					links << match
				}
			} else if(line ==~ inlineLinkMatcher) {
				def match = (line =~ inlineLinkMatcher)
				inlineIndexes[f.path] << match[0][1]
			}
		}
	}
	return [links:links.sort().unique(),
			images:images.sort().unique(),
			listedIndexes:listedIndexes,
			inlineIndexes:inlineIndexes]
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

def infileLinkErrors = 0
helpDir.eachFileRecurse(FileType.FILES) { f ->
	def listed = requestedLinksInFiles.listedIndexes[f.path].sort().unique()
	def inline = requestedLinksInFiles.inlineIndexes[f.path].sort().unique()

	if(inline - listed) {
		++infileLinkErrors
		println f.path + " >> " + listed + " vs " + inline
		println "FILE: " + f.path
		println "dead links inline: " + (inline - listed)
		//println "dead links at bottom: " + (listed - inline) 
		println "---------"
	}
}
println "FILES WITH LINK ERRORS: ${infileLinkErrors}"
