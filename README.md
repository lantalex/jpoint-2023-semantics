# Не happens-before единым: нестандартные семантики

Примеры для [доклада на JPoint 2023](https://jpoint.ru/talks/3c298184b4c2467d94d48ff9d135bb59/)

> Несмотря на то, что новые режимы упорядочивания доступов к памяти стали доступны еще с выходом JDK 9, немногие
> разработчики знакомы с ними. Хотя эти семантики все еще не имеют формального описания в модели памяти Java, они широко
> используются в популярных многопоточных библиотеках: JCTools, Disruptor, Agrona, Aeron и многих других. Поэтому для
> изучения исходного кода этих проектов нужно понимать существующие режимы упорядочивания.
>
> Но даже для исключительно прикладных разработчиков-пользователей этих библиотек понимание свойств этих режимов
> позволяет осознанно подходить к выбору используемой реализации многопоточных структур данных. Все это может
> увеличить производительность всей системы.
>
> В докладе последовательно рассматриваются все доступные на данный момент режимы: _plain_, _opaque_, _acquire-release_,
_volatile_. Для каждого показаны его высокоуровневые свойства и примеры использования.
>
>Доклад будет интересен всем, кто интересуется многопоточным программированием, моделями памяти и популярными lock-free
> библиотеками.

## Видео

[![ссылка на youtube](https://img.youtube.com/vi/UZbPOtEgcoY/0.jpg)](https://www.youtube.com/watch?v=UZbPOtEgcoY)


## Презентация

См. на странице [доклада](https://jpoint.ru/talks/3c298184b4c2467d94d48ff9d135bb59/)

## Примеры

|   Номера слайдов   | Файл                                                                                                                                                                                                                                                                                       | Комментарий                                                                                                                                                                                                                                                                                                                                     |
|:------------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 19-26,<br/>134-137 | [`VolatileSemantic_HappensBefore.java`](./src/jcstress/java/io/github/lantalex/VolatileSemantic_HappensBefore.java)                                                                                                                                                                        | Упорядочивание операций чтения/записи через _happens-before_                                                                                                                                                                                                                                                                                    |
|       53-64        | [`PlainSemantic.java`](./src/jcstress/java/io/github/lantalex/PlainSemantic.java)                                                                                                                                                                                                          | Некорректное использование _plain_-семантики, бесконечный цикл                                                                                                                                                                                                                                                                                  |
|       74-94        | [`VarHandleApi.java`](./src/main/java/io/github/lantalex/VarHandleApi.java)                                                                                                                                                                                                                | Использование ```VarHandle``` в своём коде                                                                                                                                                                                                                                                                                                      |
|      101-107       | [`OpaqueSemantic_Progress.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_Progress.java)                                                                                                                                                                                      | Демонстрация гарантии **прогресса** для _opaque_-семантики                                                                                                                                                                                                                                                                                      |
|      110-120       | [`OpaqueSemantic_Coherence.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_Coherence.java)                                                                                                                                                                                    | Переупорядочивание операций над независимыми переменными при использовании _opaque_-семантики                                                                                                                                                                                                                                                   |
|      125-129       | [`OpaqueSemantic_BitwiseAtomicity.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_BitwiseAtomicity.java)                                                                                                                                                                      | Гарантия **атомарности** операций чтения/записи в _opaque_-семантике                                                                                                                                                                                                                                                                            |
|      138-152       | [`AcquireReleaseSemantic_Causality.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_Causality.java)                                                                                                                                                                    | Упорядочивание операций чтения/записи через гарантию **причинности** с помощью _acquire-release_ семантики                                                                                                                                                                                                                                      |
|      161-172       | [`SPSC_BoundedQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_BoundedQueue.java)<br/>[`SPSC_VolatileQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_VolatileQueue.java)<br>[`SPSC_AcqRelQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_AcqRelQueue.java) | Простейшая очередь фиксированного размера для строго одного производителя и строго одного потребителя: `SPSC_BoundedQueue`. Две реализации этой очереди, через _volatile_-семантику: `SPSC_VolatileQueue`, и  через _acquire-release_-семантику: `SPSC_AcqRelQueue`                                                                             |
|      173-176       | [`QueueBenchmark.java`](./src/jmh/java/io/github/lantalex/QueueBenchmark.java)                                                                                                                                                                                                             | Бенчмарк, измеряющий пропускную способность реализованных SPSC-очередей                                                                                                                                                                                                                                                                         |
|      180-187       | [```VolatileSemantic_Consensus.java```](./src/jcstress/java/io/github/lantalex/VolatileSemantic_Consensus.java)                                                                                                                                                                            | Соревнование 🦊красной панды и 🐱кота: использование гарантии **консенсуса** с помощью _volatile_-семантики                                                                                                                                                                                                                                     |
|      189-197       | [`AcquireReleaseSemantic_NoConsensus.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_NoConsensus.java)                                                                                                                                                                | Потенциальный конфликт в итогах соревнования при использовании _acquire-release_ семантики.<br/>К сожалению, на доступном мне оборудовании конфликт не воспроизводится, но скорее всего случится на архитектуре `PowerPC`                                                                                                                       |
|        199         | [`AcquireReleaseSemantic_NoConsensus_Dekker.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_NoConsensus_Dekker.java)<br/>[`VolatileSemantic_Consensus_Dekker.java`](./src/jcstress/java/io/github/lantalex/VolatileSemantic_Consensus_Dekker.java)                    | Видоизмененный пример, теперь логика `Referee#1` находится в потоке 🐱`Cat`, логика `Referee#2` находится в потоке 🦊`Red Panda`; в результате чего возможен конфликт для _acquire-release_ семантики; для _volatile_-семантики конфликт ввиду гарантии **консенсуса** не возможен                                                              |
|                    | [`Barriers_NoConsensus_Dekker.java`](./src/jcstress/java/io/github/lantalex/Barriers_NoConsensus_Dekker.java)                                                                                                                                                                              | Контрпример для лекции   [Магистерский курс C++ (МФТИ, 2022-2023). Лекция 21. Атомики, часть 3](https://youtu.be/Y1q_Z2T2UcE?feature=shared&t=1928), слайд 107: "Явные барьеры спешат на помощь" - недостаточность семантик _opaque_ и _acquire-release_ для реализации алгоритма Деккера                                                       |
|                    | [`Single_Variable_Coherence.java`](./src/jcstress/java/io/github/lantalex/Single_Variable_Coherence.java)                                                                                                                                                                                  | Контрпример для лекции   [Магистерский курс C++ (МФТИ, 2022-2023). Лекция 21. Атомики, часть 3](https://youtu.be/Y1q_Z2T2UcE?feature=shared&t=2763), слайд 116: "Теория относительности", невозможность проявления эффектов относительности для строго одной переменной ввиду наличия гарантии когерентности для семантик от _opaque_ и сильнее |

## Конфигурация оборудования

Для запуска примеров использовалось следующее оборудование:

### Архитектура x86-64

#### Основная машина

> **CPU**: Intel Core i9 12900K (энергоэффективные ядра отключены)  
> **RAM**: 32GB DDR5-4800 MHz   
> **JVM**: 17.0.6+10-LTS  
> **OS**: Ubuntu 22.04.2 LTS/Linux 5.15.0-69-generic

### Архитектура ARM

#### ARMv8 - Raspberry Pi 4 Model B

> **CPU**: Broadcom BCM2711  
> **RAM**: 8GB LPDDR4-1500 MHz  
> **JVM**: 17.0.6+10-LTS  
> **OS**: Ubuntu 22.04.2 LTS/Linux 5.15.0-1026-raspi/arm64

#### ARMv7 - Raspberry Pi 2

> **CPU**: Broadcom BCM2836  
> **RAM**: 1GB LPDDR2-400 MHz  
> **JVM**: 17.0.6+10-LTS  
> **OS**: Raspbian GNU/Linux 5.15.84-v7+/arm