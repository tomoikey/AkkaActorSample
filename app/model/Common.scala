package model

object Common {
  case class SendToClient(msg: Long)
  case class SendToRemote(msg: Long)
  case class ResponseMsg(msg: String)
}
