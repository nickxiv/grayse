(include "pretty.lib")
(define (fib n)
    (cond 
        ((= 0 n) 
            0
        )
        ((= 1 n) 
            1
        )
        (else
            (+ (fib(- n 1)) (fib(- n 2)))
        )
    )
)

(pretty fib)
(println (fib 3))
(println (fib 4))
(println (fib 5))
(println (fib 6))
(println (fib 7))
(println (fib 8))
(println (fib 9))
(println (fib 10))
(println (fib 11))
(println (fib 12))
(println (fib 13))
(println (fib 14))
(println (fib 15))
