(define name "Grace")
(if (equal? name "Nick")
    (println "Nice")
    (println ":/")
)

(cond 
    ((equal? name "Nick") 
        (println "Nice!"))
    ((equal? name "Grace") 
        (println "OOO"))
    (else 
        (println ":/"))
    )