package ru.antoncharov;

import java.util.*;

public class LazyKing {
    private static List<String> pollResults = List.of(
            "служанка Аня",
            "управляющий Семен Семеныч: крестьянин Федя, доярка Нюра",
            "дворянин Кузькин: управляющий Семен Семеныч, жена Кузькина, экономка Лидия Федоровна",
            "экономка Лидия Федоровна: дворник Гена, служанка Аня",
            "доярка Нюра",
            "кот Василий: человеческая особь Катя",
            "дворник Гена: посыльный Тошка",
            "киллер Гена",
            "зажиточный холоп: крестьянка Таня",
            "секретарь короля: зажиточный холоп, шпион Т",
            "шпион Т: кучер Д",
            "посыльный Тошка: кот Василий",
            "аристократ Клаус",
            "просветленный Антон"
    );

    public static void main(String[] args) {
        UnluckyVassal unluckyVassal = new UnluckyVassal();

        unluckyVassal.printReportForKing(pollResults);
    }
}

/**
 * Узел хранит информацию о человеке и ссылки на его подчиненных
 * data - имя человека
 * parent - ссылка на хозяин
 * childNodes - ссылки на подчиненных
 */
class Node {
    private String data;
    private Node parent;
    private List<Node> childNodes = new ArrayList<>();

    public Node(String data) {
        this.data = data;
    }

    /**
     * Добавляет подчиненного, удаляет его из ссылок предыдущего хозяина и устанавливает хозяино текущего
     * @param node - подчиненный
     */
    public void addChild(Node node) {
        if (node.getParent() != null) {
            node.getParent().getChildNodes().remove(node);
        }
        node.setParent(this);
        childNodes.add(node);
    }

    /**
     * Ищет человека рекурсивно по имени среди текущего узла и всех потомков
     * @param data - имя человека
     * @return узел с заданным имеменм
     */
    public Optional<Node> searchNode(String data) {
        if (this.data.equals(data)) {
            return Optional.of(this);
        }
        for (Node node : childNodes) {
            Optional<Node> search = node.searchNode(data);
            if (search.isPresent()) {
                return search;
            }
        }
        return Optional.empty();
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    /**
     * Сортирует рекурсивно всех потомков по имени
     */
    public void sort() {
        childNodes.sort(Comparator.comparing(node -> node.data));
        childNodes.forEach(Node::sort);
    }

    /**
     * Формирует сроку с отступами имен текущего узла и всех потомков
     * @return строкое представление узла и всех потомков
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        Node node = this.parent;
        while (node != null) {
            res.append("    ");
            node = node.getParent();
        }
        res.append(data + System.lineSeparator());
        childNodes.forEach(n -> res.append(n.toString()));
        return res.toString();
    }
}

class UnluckyVassal {
    /**
     * Печатает список подчинённых короля
     * @param pollResults - записи всех людей с их подчинёнными
     */
    public void printReportForKing(List<String> pollResults) {
        Node king = new Node("Король");
        pollResults.forEach(pollResult -> {
            Objects.requireNonNull(pollResult, "Record must be non null");
            String[] records = pollResult.split(":");
            Node node = addServant(king, records[0]);

            if (records.length == 2) {
                addDescendants(king, node, records[1]);
            }
        });
        king.sort();
        System.out.print(king.toString());
    }

    /**
     * Добавляет подчиненного в дерево
     * @param king - король
     * @param name - имя подчиненного
     */
    public Node addServant(Node king, String name) {
        Node node = king.searchNode(name.trim())
                .orElse(new Node(name.trim()));
        if (node.getParent() == null) {
            king.addChild(node);
        }
        return node;
    }

    /**
     * Добавляет подчиненных текущего человека в дерево
     * @param king - король
     * @param current - текущий человек
     * @param servantsRecord - запись содержащая подчиненных текущего человека
     */
    public void addDescendants(Node king, Node current, String servantsRecord) {
        String[] servants = servantsRecord.split(",");
        for (String servant : servants) {
            Node childNode = king.searchNode(servant.trim())
                    .orElse(new Node(servant.trim()));
            current.addChild(childNode);
        }
    }
}
