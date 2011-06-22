package frontlinesms2

class MessageSendService {
    static transactional = true

 public void process(Fmessage message) {
  println("MessageSendService.process()")
  println("Sending message: ${message}")
  assert message instanceof Fmessage
 }
}