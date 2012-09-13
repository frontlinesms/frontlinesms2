package frontlinesms2.dev

class GroovyHayesResponse {
  final String text
  final GroovyHayesState nextState
  
  GroovyHayesResponse(String text, GroovyHayesState nextState) {
    this.text = text
    this.nextState = nextState
  }
}