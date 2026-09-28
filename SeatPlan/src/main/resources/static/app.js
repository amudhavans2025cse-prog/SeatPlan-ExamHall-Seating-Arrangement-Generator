"use strict";

// The frontend is served by Spring Boot on the same origin.
const API = "/api";

let halls = [];
let students = [];
let exams = [];
let allocations = [];

const $ = (id) => document.getElementById(id);

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, (char) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);
}

function showToast(message, isError = false) {
    const toast = $("toast");

    toast.textContent = message;
    toast.classList.toggle("error", isError);
    toast.classList.remove("hidden");

    window.clearTimeout(showToast.timer);
    showToast.timer = window.setTimeout(() => {
        toast.classList.add("hidden");
    }, 4000);
}

async function apiRequest(path, options = {}) {
    const config = {
        ...options,
        headers: {
            ...(options.body ? {
                "Content-Type": "application/json"
            } : {}),
            ...options.headers
        }
    };

    const response = await fetch(`${API}${path}`, config);

    const contentType = response.headers.get("content-type") || "";

    let data = null;

    if (response.status !== 204) {
        if (contentType.includes("application/json")) {
            data = await response.json();
        } else {
            data = await response.text();
        }
    }

    if (!response.ok) {
        const message =
            data?.message ||
            data?.error ||
            (typeof data === "string" ? data : null) ||
            `Request failed with status ${response.status}`;

        throw new Error(message);
    }

    return data;
}

function getErrorMessage(error) {
    return error?.message || "An unexpected error occurred.";
}

function formObject(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function setTableMessage(id, message, colspan) {
    $(id).innerHTML = `
        <tr>
            <td class="empty-cell" colspan="${colspan}">
                ${escapeHtml(message)}
            </td>
        </tr>
    `;
}

function renderHalls() {
    if (!halls.length) {
        setTableMessage("hallTable", "No halls registered.", 5);
        return;
    }

    $("hallTable").innerHTML = halls.map(hall => `
        <tr>
            <td>${hall.hallId}</td>
            <td>${escapeHtml(hall.hallName)}</td>
            <td>${hall.rowsCount}</td>
            <td>${hall.columnsCount}</td>
            <td>${hall.capacity}</td>
        </tr>
    `).join("");
}

function renderStudents() {
    const query = $("studentFilter").value.trim().toLowerCase();

    const filtered = students.filter(student => {
        const text = [
            student.registerNumber,
            student.studentName,
            student.className,
            student.section
        ].join(" ").toLowerCase();

        return text.includes(query);
    });

    if (!filtered.length) {
        setTableMessage(
            "studentTable",
            "No matching students found.",
            5
        );
        return;
    }

    $("studentTable").innerHTML = filtered.map(student => `
        <tr>
            <td>${student.studentId}</td>
            <td>${escapeHtml(student.registerNumber)}</td>
            <td>${escapeHtml(student.studentName)}</td>
            <td>${escapeHtml(student.className)}</td>
            <td>${escapeHtml(student.section)}</td>
        </tr>
    `).join("");
}

function renderExams() {
    if (!exams.length) {
        setTableMessage("examTable", "No examinations created.", 5);
    } else {
        $("examTable").innerHTML = exams.map(exam => `
            <tr>
                <td>${exam.examId}</td>
                <td>${escapeHtml(exam.examName)}</td>
                <td>${escapeHtml(exam.examDate)}</td>
                <td>${escapeHtml(exam.startTime || "-")}</td>
                <td>${escapeHtml(exam.endTime || "-")}</td>
            </tr>
        `).join("");
    }

    const selects = [
        "examSelect",
        "generateExamSelect",
        "lookupExamSelect"
    ];

    selects.forEach(id => {
        const select = $(id);
        const previousValue = select.value;

        select.innerHTML = `
            <option value="">Choose an exam</option>
            ${exams.map(exam => `
                <option value="${exam.examId}">
                    ${escapeHtml(exam.examName)}
                    (ID: ${exam.examId})
                </option>
            `).join("")}
        `;

        if (exams.some(
            exam => String(exam.examId) === previousValue
        )) {
            select.value = previousValue;
        }
    });
}

function renderRecentExams() {
    if (!exams.length) {
        setTableMessage("recentExamTable", "No examinations created yet.", 6);
        return;
    }

    const recent = [...exams]
        .sort((first, second) => Number(second.examId) - Number(first.examId))
        .slice(0, 5);

    $("recentExamTable").innerHTML = recent.map(exam => `
        <tr>
            <td>${exam.examId}</td>
            <td>${escapeHtml(exam.examName)}</td>
            <td>${escapeHtml(exam.examDate)}</td>
            <td>${escapeHtml(exam.startTime || "-")}</td>
            <td>${escapeHtml(exam.endTime || "-")}</td>
            <td><button class="primary generate-button" type="button" data-generate-exam="${exam.examId}">Generate Seating</button></td>
        </tr>
    `).join("");
}

function renderStudentOptions() {
    $("studentMultiSelect").innerHTML = students.map(student => `
        <option value="${student.studentId}">
            ${escapeHtml(student.registerNumber)}
            — ${escapeHtml(student.studentName)}
            (${escapeHtml(student.className)}-${escapeHtml(student.section)})
        </option>
    `).join("");
}

function renderStats() {
    $("hallCount").textContent = halls.length;
    $("studentCount").textContent = students.length;
    $("examCount").textContent = exams.length;
    $("allocationCount").textContent = allocations.length;
}

async function refreshAll() {
    try {
        const [hallData, studentData, examData] =
            await Promise.all([
                apiRequest("/halls"),
                apiRequest("/students"),
                apiRequest("/exams")
            ]);

        halls = hallData;
        students = studentData;
        exams = examData;

        renderHalls();
        renderStudents();
        renderExams();
        renderStudentOptions();
        renderStats();
        renderRecentExams();

        showToast("Dashboard refreshed.");
    } catch (error) {
        showToast(
            "Could not load dashboard: " + getErrorMessage(error),
            true
        );
    }
}

function showPage(pageName) {
    document.querySelectorAll(".nav-btn").forEach(button => {
        button.classList.toggle("active", button.dataset.page === pageName);
    });

    document.querySelectorAll(".page").forEach(page => {
        page.classList.toggle("hidden", page.id !== pageName);
    });
}

// Navigation
document.querySelectorAll("[data-page]").forEach(button => {
    button.addEventListener("click", () => {
        showPage(button.dataset.page);
    });
});

// Create hall
$("hallForm").addEventListener("submit", async event => {
    event.preventDefault();

    const form = formObject(event.target);

    const rowsCount = Number(form.rowsCount);
    const columnsCount = Number(form.columnsCount);
    const capacity = rowsCount * columnsCount;

    if (
        !Number.isInteger(rowsCount) ||
        !Number.isInteger(columnsCount) ||
        rowsCount < 1 ||
        columnsCount < 1
    ) {
        showToast("Rows and columns must be positive integers.", true);
        return;
    }

    try {
        await apiRequest("/halls", {
            method: "POST",
            body: JSON.stringify({
                hallName: form.hallName.trim(),
                rowsCount,
                columnsCount,
                capacity
            })
        });

        event.target.reset();
        await refreshDataSilently();
        showToast("Hall created successfully.");
    } catch (error) {
        showToast(getErrorMessage(error), true);
    }
});

// Register student
$("studentForm").addEventListener("submit", async event => {
    event.preventDefault();

    const form = formObject(event.target);

    try {
        await apiRequest("/students", {
            method: "POST",
            body: JSON.stringify({
                registerNumber: form.registerNumber.trim(),
                studentName: form.studentName.trim(),
                className: form.className.trim(),
                section: form.section.trim()
            })
        });

        event.target.reset();
        await refreshDataSilently();
        showToast("Student registered successfully.");
    } catch (error) {
        showToast(getErrorMessage(error), true);
    }
});

// Search student table
$("studentFilter").addEventListener("input", renderStudents);

// Create exam
$("examForm").addEventListener("submit", async event => {
    event.preventDefault();

    const form = formObject(event.target);

    if (form.startTime >= form.endTime) {
        showToast("End time must be later than start time.", true);
        return;
    }

    try {
        await apiRequest("/exams", {
            method: "POST",
            body: JSON.stringify({
                examName: form.examName.trim(),
                examDate: form.examDate,
                startTime: form.startTime,
                endTime: form.endTime
            })
        });

        event.target.reset();
        await refreshDataSilently();
        showToast("Examination created successfully.");
    } catch (error) {
        showToast(getErrorMessage(error), true);
    }
});

// Register selected students for an exam
$("examStudentsForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        const examId = $("examSelect").value;

        const studentIds = Array.from(
            $("studentMultiSelect").selectedOptions
        ).map(option => Number(option.value));

        if (!examId) {
            showToast("Select an examination.", true);
            return;
        }

        if (!studentIds.length) {
            showToast("Select at least one student.", true);
            return;
        }

        try {
            await apiRequest(`/exams/${examId}/students`, {
                method: "POST",
                body: JSON.stringify(studentIds)
            });

            showToast("Students registered for the exam.");
        } catch (error) {
            showToast(getErrorMessage(error), true);
        }
    }
);

// Generate seating plan
async function generateSeatingForExam(examId) {
    try {
        const result = await apiRequest(
            `/allocations/generate/${encodeURIComponent(examId)}`,
            { method: "POST" }
        );

        allocations = result;
        $("generateExamSelect").value = String(examId);
        renderSeatingChart(result);
        renderStats();
        showPage("seating");
        showToast("Seating plan generated successfully.");
    } catch (error) {
        showToast(getErrorMessage(error), true);
    }
}

$("recentExamTable").addEventListener("click", async event => {
    const button = event.target.closest("[data-generate-exam]");
    if (button) await generateSeatingForExam(button.dataset.generateExam);
});

$("generateForm").addEventListener("submit", async event => {
    event.preventDefault();

    const examId = $("generateExamSelect").value;
    if (!examId) {
        showToast("Select an examination.", true);
        return;
    }

    await generateSeatingForExam(examId);
});

function renderSeatingChart(data) {
    $("seatingSummary").textContent =
        `${data.length} students allocated. ` +
        `Halls used: ${new Set(
            data.map(item => item.seat.hall.hallId)
        ).size}.`;

    if (!data.length) {
        $("seatingChart").innerHTML = `
            <div class="panel">
                No seating allocations were returned.
            </div>
        `;
        return;
    }

    const grouped = new Map();

    data.forEach(allocation => {
        const hall = allocation.seat.hall;
        const hallId = hall.hallId;

        if (!grouped.has(hallId)) {
            grouped.set(hallId, {
                hall,
                allocations: []
            });
        }

        grouped.get(hallId).allocations.push(allocation);
    });

    $("seatingChart").innerHTML = [...grouped.values()]
        .map(({ hall, allocations: hallAllocations }) => {
            const seatsByPosition = new Map();

            hallAllocations.forEach(allocation => {
                const seat = allocation.seat;

                seatsByPosition.set(
                    `${seat.rowNumber}-${seat.columnNumber}`,
                    allocation
                );
            });

            let rows = "";

            for (let row = 1; row <= hall.rowsCount; row++) {
                let cells = "";

                for (let column = 1; column <= hall.columnsCount; column++) {
                    const allocation = seatsByPosition.get(`${row}-${column}`);

                    if (allocation) {
                        const student = allocation.student;
                        const seat = allocation.seat;

                        cells += `
                            <td>
                                <span class="seat-number">${escapeHtml(seat.seatNumber)}</span>
                                <span class="seat-student">${escapeHtml(student.studentName)}</span>
                                <span class="seat-section">${escapeHtml(student.className)}-${escapeHtml(student.section)}</span>
                            </td>
                        `;
                    } else {
                        cells += `
                            <td class="unallocated">
                                <span class="seat-number">
                                    R${row}C${column}
                                </span>
                                <span class="seat-student">
                                    Empty
                                </span>
                            </td>
                        `;
                    }
                }

                rows += `<tr>${cells}</tr>`;
            }

            return `
                <div class="hall-chart">
                    <h3>${escapeHtml(hall.hallName)}</h3>
                    <p class="muted">
                        Capacity: ${hall.capacity} |
                        Allocated: ${hallAllocations.length}
                    </p>
                    <div class="hall-front">FRONT / INVIGILATOR</div>
                    <table class="exam-desk">
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            `;
        }).join("");
}

// Find a student's allocated seat
$("lookupForm").addEventListener(
    "submit",
    async event => {
        event.preventDefault();

        const registerNumber =
            $("lookupRegister").value.trim();

        const examId = $("lookupExamSelect").value;

        if (!examId) {
            showToast("Select an examination.", true);
            return;
        }

        try {
            const allocation = await apiRequest(
                `/allocations/student/` +
                `${encodeURIComponent(registerNumber)}` +
                `?examId=${encodeURIComponent(examId)}`
            );

            const student = allocation.student;
            const seat = allocation.seat;
            const hall = seat.hall;

            $("lookupResult").innerHTML = `
                <div class="result-card">
                    <h3>Seat Allocation Found</h3>
                    <p><strong>Name:</strong>
                        ${escapeHtml(student.studentName)}</p>
                    <p><strong>Register Number:</strong>
                        ${escapeHtml(student.registerNumber)}</p>
                    <p><strong>Class:</strong>
                        ${escapeHtml(student.className)} -
                        ${escapeHtml(student.section)}</p>
                    <p><strong>Exam:</strong>
                        ${escapeHtml(allocation.examSession.examName)}</p>
                    <p><strong>Hall:</strong>
                        ${escapeHtml(hall.hallName)}</p>
                    <p><strong>Seat:</strong>
                        ${escapeHtml(seat.seatNumber)}</p>
                    <p><strong>Row:</strong> ${seat.rowNumber}</p>
                    <p><strong>Column:</strong>
                        ${seat.columnNumber}</p>
                </div>
            `;
        } catch (error) {
            $("lookupResult").innerHTML = `
                <div class="result-card">
                    ${escapeHtml(getErrorMessage(error))}
                </div>
            `;
        }
    }
);

function printSeating() {
    if (!allocations.length) {
        showToast("Generate a seating plan before printing.", true);
        return;
    }

    window.print();
}

async function refreshDataSilently() {
    const [hallData, studentData, examData] =
        await Promise.all([
            apiRequest("/halls"),
            apiRequest("/students"),
            apiRequest("/exams")
        ]);

    halls = hallData;
    students = studentData;
    exams = examData;

    renderHalls();
    renderStudents();
    renderExams();
    renderStudentOptions();
    renderStats();
}

// Initial page load
document.addEventListener("DOMContentLoaded", () => {
    refreshAll();
});