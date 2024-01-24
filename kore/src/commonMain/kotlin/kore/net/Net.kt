package kore.net

class Net(private val endPoint:EndPoint){
    private val requests:ArrayList<Request> = ArrayList()
    private val responses:ArrayList<Response> = ArrayList()
    operator fun plus(req:Request):Net{
        requests.add(req)
        return this
    }
    operator fun plus(reqs:List<Request>):Net{
        requests.addAll(reqs)
        return this
    }
    operator fun plus(res:Response):Net{
        responses.add(res)
        return this
    }
    operator fun plus(res:List<Response>):Net{
        responses.addAll(res)
        return this
    }
}
class EndPoint {}
class Response {}
class Request {}
