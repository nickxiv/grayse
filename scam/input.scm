(println "Input char")
(define c (readChar))
(inspect c)

(println "Input int")
(define i (readInt))
(inspect i)

(println "Input real")
(define r (readReal))
(inspect r)

(println "Input String")
(define s (readString))
(inspect s)

(println "Input token")
(define t (readToken))
(inspect t)

(println "Input raw char")
(define raw (readRawChar))
(inspect raw)

(println "Input read until p")
(define u (readUntil "p"))
(inspect u)

(println "Input readWhile p")
(define w (readWhile "o"))
(inspect w)

(print ln "Input line")
(define l (readLine))
(inspect l)