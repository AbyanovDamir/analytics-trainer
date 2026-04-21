package main

import (
    "github.com/gofiber/fiber/v2"
    "github.com/gofiber/fiber/v2/middleware/logger"
    "github.com/gofiber/fiber/v2/middleware/recover"
    "log"
    "time"
)

type TestCheckRequest struct {
    TaskContent map[string]interface{} `json:"task_content"`
    UserAnswer  []int                  `json:"user_answer"`
}

type ErrorSpottingRequest struct {
    TaskContent map[string]interface{} `json:"task_content"`
    UserAnswer  []string               `json:"user_answer"`
}

type CheckResponse struct {
    Score   int                    `json:"score"`
    Details map[string]interface{} `json:"details,omitempty"`
    TimeMs  int64                  `json:"time_ms,omitempty"`
}

func main() {
    app := fiber.New(fiber.Config{
        ServerHeader: "AnalyticsTrainer",
        ReadTimeout:  10 * time.Second,
        WriteTimeout: 10 * time.Second,
    })

    app.Use(logger.New(logger.Config{
        Format: "[${time}] ${status} - ${method} ${path} - ${latency}\n",
    }))
    app.Use(recover.New())

    app.Post("/check/test", func(c *fiber.Ctx) error {
        start := time.Now()
        var req TestCheckRequest
        if err := c.BodyParser(&req); err != nil {
            return c.Status(400).JSON(fiber.Map{"error": "Invalid request"})
        }
        correct, ok := req.TaskContent["correct"].([]interface{})
        if !ok || len(correct) == 0 {
            return c.JSON(CheckResponse{Score: 0, TimeMs: time.Since(start).Milliseconds()})
        }
        correctSet := make(map[int]bool, len(correct))
        for _, v := range correct {
            switch num := v.(type) {
            case float64:
                correctSet[int(num)] = true
            case int:
                correctSet[num] = true
            }
        }
        correctCount := 0
        for _, answer := range req.UserAnswer {
            if correctSet[answer] {
                correctCount++
            }
        }
        maxScore := 100
        if ms, ok := req.TaskContent["max_score"].(float64); ok {
            maxScore = int(ms)
        }
        total := len(correct)
        score := 0
        if total > 0 {
            score = int((float64(correctCount) / float64(total)) * float64(maxScore))
        }
        return c.JSON(CheckResponse{
            Score:   score,
            TimeMs:  time.Since(start).Milliseconds(),
            Details: fiber.Map{"correct_count": correctCount, "total": total},
        })
    })

    app.Post("/check/error-spotting", func(c *fiber.Ctx) error {
        start := time.Now()
        var req ErrorSpottingRequest
        if err := c.BodyParser(&req); err != nil {
            return c.Status(400).JSON(fiber.Map{"error": "Invalid request"})
        }
        expected, ok := req.TaskContent["expected_errors"].([]interface{})
        if !ok {
            return c.JSON(CheckResponse{Score: 0, TimeMs: time.Since(start).Milliseconds()})
        }
        expectedSet := make(map[string]bool, len(expected))
        for _, v := range expected {
            if str, ok := v.(string); ok {
                expectedSet[str] = true
            }
        }
        foundCount := 0
        for _, found := range req.UserAnswer {
            if expectedSet[found] {
                foundCount++
            }
        }
        maxScore := 100
        if ms, ok := req.TaskContent["max_score"].(float64); ok {
            maxScore = int(ms)
        }
        total := len(expected)
        score := 0
        if total > 0 {
            score = int((float64(foundCount) / float64(total)) * float64(maxScore))
        }
        return c.JSON(CheckResponse{
            Score:   score,
            TimeMs:  time.Since(start).Milliseconds(),
            Details: fiber.Map{"found": foundCount, "expected": total},
        })
    })

    app.Get("/health", func(c *fiber.Ctx) error {
        return c.JSON(fiber.Map{
            "status": "ok",
            "version": "go1.22.2",
            "service": "analytics-checker",
        })
    })

    app.Get("/metrics", func(c *fiber.Ctx) error {
        return c.JSON(fiber.Map{
            "go_version": "1.22.2",
            "fiber_version": "2.52.5",
            "uptime": time.Now().Unix(),
        })
    })

    log.Println("Auto-checker service started on port 8081")
    log.Fatal(app.Listen(":8081"))
}
