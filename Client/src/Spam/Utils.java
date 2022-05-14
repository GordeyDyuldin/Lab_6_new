package Spam;

/**
 * Вспомогательные утилиты
 */

public class Utils {

    /**
     * Преобразование строки в Spam.Spam.Difficulty
     * @param difficultyName имя организации
     * @return объект перечисления Spam.Spam.Difficulty или null
     */

    public static Difficulty StrToType(String difficultyName)
    {
        try
        {
            return Difficulty.valueOf(difficultyName.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }
}