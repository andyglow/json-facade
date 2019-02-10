package json.facade

trait WriteF[-T] { def write(x: T): Value }