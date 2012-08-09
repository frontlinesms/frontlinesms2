#!/usr/bin/env groovy
 
// this script finds the test class that's causing test pollution in other tests.  Just pass it
// a file name containing the test order that is failing and the particular test that is failing
// it will start doing a binary search by running combinations of tests that occur before the failing test
// and will eventually identify the smallest set of tests that are failing when run together
class BisectGrailsTests {
 
    String testOrderFilePath
    File testOrderFile
    String pollutedTest
    String testPhase = null
    List<String> lastFailingTestList
 
    public BisectGrailsTests(args) {
        parseArgs(args)
    }
 
    def parseArgs(args) {
        def cli = new CliBuilder(usage: "bisectGrailsTests.groovy -f testFile.txt -t com.example.FailingTest")
 
        cli.with {
            f longOpt: 'testOrderFilePath', args: 1, argName: 'filePath', "A file containing the test class names in the order you'd like to run them"
            t longOpt: 'pollutedTest', args: 1, argName: 'pollutedTest', "The test class name that passes when run by itself but fails when the other tests are run"
            p longOpt: 'testPhase', args: 1, argName: 'testPhase', "Only run with a particular test phase (ex: 'unit', 'integration', etc)"
        }
 
        def options = cli.parse(args)
 
        if(options.'testOrderFilePath') {
            testOrderFilePath = options.'testOrderFilePath'
            testOrderFile = new File(testOrderFilePath)
        }
 
        if (options.'pollutedTest') pollutedTest = options.'pollutedTest'
 
        if (options.'testPhase') {
            testPhase = options.'testPhase'
            println "Only running test phase $testPhase"
        }
 
        if (!pollutedTest || !testOrderFilePath) {
            cli.usage()
            System.exit(-1)
        } else if (!testOrderFile.exists()) {
            println "Unable to find test order file from given path: $testOrderFilePath"
            cli.usage()
            System.exit(-1)
        }
    }
 
    void bisectTests() {
        // run test by itself to ensure it passes
        if (!testsPass("solo", [pollutedTest])) {
            println "The passed test ($pollutedTest) fails when run by itself, there isn't test pollution we can detect if it's failing on it's own"
            println "Here's the command to execute to see if you've fixed things:\n" + createCommand(withPollutedTest([pollutedTest]))
            return
        }
 
        // now recursively run test with other tests and bisect till it fails
        bisectTests(truncatedTestList)
    }
 
    List<String> bisectTests(List<String> testList) {
 
        // if the test list size is > 1, split into left and right hand sides
        if (testList.size() > 1) {
            List<String> lhs = leftHandSide(testList)
            List<String> rhs = rightHandSide(testList)
 
            if (!testsPass("left", withPollutedTest(lhs)))  return bisectTests(lhs)
 
            if (!testsPass("right", withPollutedTest(rhs)))  return bisectTests(rhs)
 
            if (testsPass("full", withPollutedTest(testList))) {
                println "Unable to find any failing tests with this list, running them all appears to run without issue: \n" + testList.join(" ")
                return null
            }
 
            println "Unable to find just one test that's causing problems.  Running just with the left hand or right hand side of this narrowed list passes, but the full list fails"
            println "full list (fails): " + testList.join(" ")
            println "left hand side (passes): " + lhs.join(" ")
            println "right hand side (passes): " + rhs.join(" ")
        } else if (!testsPass("suspected", withPollutedTest(testList))){
            println "The test that's causing pollution: " + testList.join(" ") // should only be 1
            println "Here's the command to execute to see if you've fixed things:\n" + createCommand(withPollutedTest(testList))
            return testList
        }
        println "Not sure what's happening, got to this list of tests, but everything passes with this list: \n" + testList.join(" ")
    }
 
    List<String> leftHandSide(List<String> testList) {
        return testList[0..(testList.size()/2 - 1)]
    }
 
    List<String> rightHandSide(List<String> testList) {
        return testList[(testList.size()/2)..-1]
    }
 
    List<String> withPollutedTest(List<String> testList) {
        return [testList, pollutedTest].flatten()
    }
 
    List<String> getTruncatedTestList() {
        List<String> testList = []
 
        testOrderFile.eachLine { String line -> testList << line.trim() }
 
        Integer badTestAt = testList.indexOf(pollutedTest)
 
        if (badTestAt < 0) {
            // it's not in the list, don't truncate it and we'll just append it on the end when we need it
            return testList
        } else if (badTestAt == 0) {
            println "The bad test is the first test in the file list, it cannot be test pollution that is affecting this test"
            System.exit(-1)
        }
        return testList[0..<badTestAt]
    }
 
    Boolean testsPass(String runName, List<String> testList) {
        String command = createCommand(testList)
        return runCommand(runName, command) == 0
    }
 
    String createCommand(List<String> testList) {
        String command = "grails test-app "
        if (testPhase) command += "$testPhase: "
        return command + testList.join(" ")
    }
 
    def out(prefix, message) {
        println("${prefix.padLeft(12, ' ')}: $message")
    }
 
    Integer runCommand(runName, command) {
        out runName, command
        return command.execute().with { proc ->
            proc.in.eachLine { line -> out runName, line }
            proc.waitFor()
            proc.exitValue()
        }
    }
}
 
new BisectGrailsTests(args).bisectTests()