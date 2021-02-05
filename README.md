# Пример использования git bisect для поиска причины возникновения багов

###### Допустим, имеется следующая история коммитов (от самого нового к старым):

1. 897d7083ec1384fd0b465ebc3947013380d14ce0
test commit 14

2. 4d56523c4c0f7b1e67b5ae82695abb298ae82b24
test commit 13

3. 75c5a98722bde810a5075e7fe42d04c3e79a788f
test commit 12

4. e87a931f3104d72482782ac851b4025fdc20ffa1
test commit 11

5. 494fee8358915eb088254499024262b39a9e5e42
test commit 10

6. 58559d38a7fceefc538af3e61cc78810e93b3cf1
test commit 9

7. 97c5fd3baf841abd20fbf50d550be2bc4091329e
test commit 8

8. 75e88725968dc143383191317a82c8f6d60fc5dc
test commit 7

9. commit da75abecea7f115a92ee321198e638bfc132b6fe
test commit 6

10. 69eeff42711f4681b43e32871673ff74f9551d2d
test commit 5

11. 40606d448e220ebed7154e2fc7722686cad0006e
bad commit

12. ea93e7642ab1c8b3c7bebdfe9303cb0f9e2f8c9f
test commit 4

13. 1b530310428ad4f61b2b528d42cada81d117053c
test commit 3

14. 626153b9ad9ab76b5e665443704fc5b75eebfd4a
test commit 2

15. b319cc6c627fbda9b0d6c1ec3c2c828471ead819
test commit 1

16. 7168f1df0061473dbe94ca25ec4cceebe42fbe55
test add

17. 4984f224312b4e243086cf381ed45c6e923067fe
init

###### Линейный поиск

Можно поочереди производить переключение на каждый коммит через checkout и смотреть воспроизводится ли баг.
В нашем случае это займёт 11 шагов. Линейная сложность - O(n)

1. 897d7083ec1384fd0b465ebc3947013380d14ce0
test commit 14

2. 4d56523c4c0f7b1e67b5ae82695abb298ae82b24
test commit 13

3. 75c5a98722bde810a5075e7fe42d04c3e79a788f
test commit 12

4. e87a931f3104d72482782ac851b4025fdc20ffa1
test commit 11

5. 494fee8358915eb088254499024262b39a9e5e42
test commit 10

6. 58559d38a7fceefc538af3e61cc78810e93b3cf1
test commit 9

7. 97c5fd3baf841abd20fbf50d550be2bc4091329e
test commit 8

8. 75e88725968dc143383191317a82c8f6d60fc5dc
test commit 7

9. commit da75abecea7f115a92ee321198e638bfc132b6fe
test commit 6

10. 69eeff42711f4681b43e32871673ff74f9551d2d
test commit 5

11. 40606d448e220ebed7154e2fc7722686cad0006e
bad commit

###### Логарифмический поиск

Но можно ускорить этот процесс, если воспользоваться бинарным алгоритмом. Будем указывать интервал и делить его на 2 части пока не найдём нужный коммит. Логарифмическая сложность - O(log(n))

Начинаем

`git bisect start`

Допустим, текущий коммит (HEAD) плохой. Указываем

`git bisect bad`

Указываем коммит, на котором баг не воспроизводится. Я точно знаю, что когда я добавил тесты, то всё было в порядке. Это был коммит 7168f1df0061473dbe94ca25ec4cceebe42fbe55

`git bisect good 7168f1df`

Это 14 ревизий. Git сразу переключается на середину и сообщает, что осталось посмотреть 7 ревизий. Это примерно 3 шага:
`Bisecting: 7 revisions left to test after this (roughly 3 steps)
 [da75abecea7f115a92ee321198e638bfc132b6fe] test commit 6
`

Прогоняем тесты / пытаемся воспроизвести баг вручную

`./gradlew test`

Видим, что тесты всё ещё падают. Идём дальше

`git bisect bad`

Git опять поделил ревизии пополам и переключился в середину: `Bisecting: 3 revisions left to test after this (roughly 2 steps)
                                                              [1b530310428ad4f61b2b528d42cada81d117053c] test commit 3
`

Прогоняем тесты / пытаемся воспроизвести баг вручную

`./gradlew test`

Тесты успешно прошли. Помечаем этот коммит как хороший

`git bisect good`

Git выводит информацию о том, какой коммит был причиной наших нерабочих тестов и переключается на него, чтобы можно было поправить: `Bisecting: 1 revision left to test after this (roughly 1 step)
                                                                                                                                     [40606d448e220ebed7154e2fc7722686cad0006e] bad commit`
                                                                                                                                     
Вывод: вместо 11 шагов мы прошли 3, что позволило намного быстрее найти причину бага