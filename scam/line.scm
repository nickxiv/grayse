(define (createLine slope intercept)
    (define (y x)
        (+ intercept (* slope x))
    )
    y
)

(define line1 (createLine 5 -3))
(define line2 (createLine 6 2))
(inspect line1)
(inspect line2)

(inspect (line1 9))
(inspect (line2 9))