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

## Презентация

TBD

## Примеры

|   Номера слайдов   | Файл                                                                                                                                                                                                                                                                                       | Комментарий                                                                                                                                                                                                                                                                        |
|:------------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 17-24,<br/>125-128 | [`VolatileSemantic_HappensBefore.java`](./src/jcstress/java/io/github/lantalex/VolatileSemantic_HappensBefore.java)                                                                                                                                                                        | Упорядочивание операций чтения/записи через _happens-before_                                                                                                                                                                                                                       |
|       53-64        | [`PlainSemantic.java`](./src/jcstress/java/io/github/lantalex/PlainSemantic.java)                                                                                                                                                                                                          | Некорректное использование _plain_-семантики, бесконечный цикл                                                                                                                                                                                                                     |
|       70-89        | [`VarHandleApi.java`](./src/main/java/io/github/lantalex/VarHandleApi.java)                                                                                                                                                                                                                | Использование ```VarHandle``` в своём коде                                                                                                                                                                                                                                         |
|       91-99        | [`OpaqueSemantic_Progress.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_Progress.java)                                                                                                                                                                                      | Демонстрация гарантии **прогресса** для _opaque_-семантики                                                                                                                                                                                                                         |
|      102-112       | [`OpaqueSemantic_Coherence.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_Coherence.java)                                                                                                                                                                                    | Переупорядочивание операций над независимыми переменными при использовании _opaque_-семантики                                                                                                                                                                                      |
|      116-120       | [`OpaqueSemantic_BitwiseAtomicity.java`](./src/jcstress/java/io/github/lantalex/OpaqueSemantic_BitwiseAtomicity.java)                                                                                                                                                                      | Гарантия **атомарности** операций чтения/записи в _opaque_-семантике                                                                                                                                                                                                               |
|      129-140       | [`AcquireReleaseSemantic_Causality.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_Causality.java)                                                                                                                                                                    | Упорядочивание операций чтения/записи через гарантию **причинности** с помощью _acquire-release_ семантики                                                                                                                                                                         |
|      140-152       | [`SPSC_BoundedQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_BoundedQueue.java)<br/>[`SPSC_VolatileQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_VolatileQueue.java)<br>[`SPSC_AcqRelQueue.java`](./src/main/java/io/github/lantalex/queue/SPSC_AcqRelQueue.java) | Простейшая очередь фиксированного размера для сторого одного производителя и сторого одного потребителя: `SPSC_BoundedQueue`. Две реализации этой очереди, через _volatile_-семантику: `SPSC_VolatileQueue`, и  через _acquire-release_-семантику: `SPSC_AcqRelQueue`              |
|      153-155       | [`QueueBenchmark.java`](./src/jmh/java/io/github/lantalex/QueueBenchmark.java)                                                                                                                                                                                                             | Бенчмарк, измеряющий пропускную способность реализованных SPSC-очередей                                                                                                                                                                                                            |
|  158-167,<br/>177  | [```VolatileSemantic_Consensus.java```](./src/jcstress/java/io/github/lantalex/VolatileSemantic_Consensus.java)                                                                                                                                                                            | Соревнование 🦊красной панды и 🐱кота: использование гарантии **консенсуса** с помощью _volatile_-семантики                                                                                                                                                                        |
|      168-175       | [`AcquireReleaseSemantic_NoConsensus.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_NoConsensus.java)                                                                                                                                                                | Потенциальный конфликт в итогах соревнования при использовании _acquire-release_ семантики.<br/>К сожалению, на доступном мне оборудовании конфликт не воспроизводится, но скорее всего, возможен на `PowerPC`                                                                     |
|      168-175       | [`AcquireReleaseSemantic_NoConsensus_Dekker.java`](./src/jcstress/java/io/github/lantalex/AcquireReleaseSemantic_NoConsensus_Dekker.java)<br/>[`VolatileSemantic_Consensus_Dekker.java`](./src/jcstress/java/io/github/lantalex/VolatileSemantic_Consensus_Dekker.java)                    | Видоизмененный пример, теперь логика `Referee#1` находится в потоке 🐱`Cat`, логика `Referee#2` находится в потоке 🦊`Red Panda`; в результате чего возможен конфликт для _acquire-release_ семантики; для _volatile_-семантики конфликт ввиду гарантии **консенсуса** не возможен |

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