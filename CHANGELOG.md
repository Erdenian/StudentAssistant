# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Значительно уменьшено время получения списка занятий в определенный день из БД

## [0.7.3] - 2025-11-27

### Fixed
- Цвет иконок в статусбаре и навбаре на Android 5

## [0.7.2] - 2025-11-23

Нет видимых изменений

## [0.7.1] - 2025-07-02

Нет видимых изменений

## [0.7.0] - 2024-11-12

### Fixed
- Отображение текстовки "Домашние задания" в строке навигации
- Выделение выбранной вкладки в строке навигации
- Проблемы при многократном нажатии кнопки "Назад" в тулбаре
- Положение контекстного меню при первом открытии внизу экрана
- Состояние выбора повторений при открытии экрана редактирования занятия
- Цвет статусбара при скролле контента

### Added
- Кнопка перехода к сегодняшней дате в диалоге выбора даты
- Поддержка predictive back gesture
- Shared element transition при переходе на экран информации о занятии
- В редакторе занятия уже введенные преподаватели и аудитории больше не будут предлагаться в автодополнении
- Анимации добавления и удаления занятий и заданий

## [0.6.0] - 2024-03-31

### Fixed
- Навигация нижнего бара
- Видимость кнопки удаления в редакторе занятия

### Changed
- Дизайн приложения обновлен до Material 3

## [0.5.3] - 2023-02-06

Нет видимых изменений

## [0.5.2] - 2022-06-22

Нет видимых изменений

## [0.5.1] - 2022-04-09

### Added
- Упрощенный выбор недель для повторения занятия

### Changed
- ExposedDropdownMenu переписан на Compose
- AutoCompleteTextField переписан на Compose
- MultiAutoCompleteTextField переписан на Compose
- Использование MaterialAlertDialog переписано на Compose

## [0.5.0] - 2022-02-16

### Changed
- Приложение полностью переписано на Jetpack Compose

## [0.4.5] - 2021-04-15

### Fixed
- Отображение длинных домашних заданий в карточке домашнего задания

### Added
- Экран настроек

## [0.4.4] - 2021-02-27

### Fixed
- Цвет кнопок в навбаре для API < 27

## [0.4.3] - 2020-11-25

Нет видимых изменений

## [0.4.2] - 2020-08-20

Нет видимых изменений

## [0.4.1] - 2020-06-10

### Fixed
- Создание домашнего задания из LessonInformationFragment
- Обновление меню на некоторых фрагментах
- Пустое пространство в CheckBoxWithText
- Переход к следующему полю в LessonEditorFragment

### Changed
- Верстка SemesterEditorFragment
- Верстка LessonEditorFragment
- Верстка HomeworkEditorFragment

## [0.4.0] - 2020-05-03

Первый релиз в Google Play

## [0.3.1] - 2019-08-18

## [0.3.0] - 2019-08-04

## [0.2.9] - 2019-01-22

## [0.2.8] - 2018-10-14

### Changed
- minSdkVersion повышена до 21

## [0.2.7] - 2017-09-24

## [0.2.6] - 2017-03-23

## [0.2.5] - 2017-03-23

## [0.2.4] - 2017-03-21

## [0.2.3] - 2017-03-13

### Changed
- Минимальная версия системы повышена до Jelly Bean

## [0.2.0] - 2016-12-22

## [0.1.0] - 2016-12-19

<!-- @formatter:off -->
[Unreleased]: https://github.com/Erdenian/StudentAssistant/compare/0.7.3...develop
[0.7.3]: https://github.com/Erdenian/StudentAssistant/compare/0.7.2...0.7.3
[0.7.2]: https://github.com/Erdenian/StudentAssistant/compare/0.7.1...0.7.2
[0.7.1]: https://github.com/Erdenian/StudentAssistant/compare/0.7.0...0.7.1
[0.7.0]: https://github.com/Erdenian/StudentAssistant/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/Erdenian/StudentAssistant/compare/0.5.3...0.6.0
[0.5.3]: https://github.com/Erdenian/StudentAssistant/compare/0.5.2...0.5.3
[0.5.2]: https://github.com/Erdenian/StudentAssistant/compare/0.5.1...0.5.2
[0.5.1]: https://github.com/Erdenian/StudentAssistant/compare/0.5.0...0.5.1
[0.5.0]: https://github.com/Erdenian/StudentAssistant/compare/0.4.5...0.5.0
[0.4.5]: https://github.com/Erdenian/StudentAssistant/compare/0.4.4...0.4.5
[0.4.4]: https://github.com/Erdenian/StudentAssistant/compare/0.4.3...0.4.4
[0.4.3]: https://github.com/Erdenian/StudentAssistant/compare/0.4.2...0.4.3
[0.4.2]: https://github.com/Erdenian/StudentAssistant/compare/0.4.1...0.4.2
[0.4.1]: https://github.com/Erdenian/StudentAssistant/compare/0.4.0...0.4.1
[0.4.0]: https://github.com/Erdenian/StudentAssistant/compare/0.3.1...0.4.0
[0.3.1]: https://github.com/Erdenian/StudentAssistant/compare/0.3.0...0.3.1
[0.3.0]: https://github.com/Erdenian/StudentAssistant/compare/0.2.9...0.3.0
[0.2.9]: https://github.com/Erdenian/StudentAssistant/compare/0.2.8...0.2.9
[0.2.8]: https://github.com/Erdenian/StudentAssistant/compare/0.2.7...0.2.8
[0.2.7]: https://github.com/Erdenian/StudentAssistant/compare/0.2.6...0.2.7
[0.2.6]: https://github.com/Erdenian/StudentAssistant/compare/0.2.5...0.2.6
[0.2.5]: https://github.com/Erdenian/StudentAssistant/compare/0.2.4...0.2.5
[0.2.4]: https://github.com/Erdenian/StudentAssistant/compare/0.2.3...0.2.4
[0.2.3]: https://github.com/Erdenian/StudentAssistant/compare/0.2.0...0.2.3
[0.2.0]: https://github.com/Erdenian/StudentAssistant/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/Erdenian/StudentAssistant/compare/e616d052ad609f694c1dd9c4492a758597fd8f3f...0.1.0
<!-- @formatter:on -->
