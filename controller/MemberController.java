package controller;

import javafx.collections.ObservableList;
import model.Member;
import storage.DataStore;

public class MemberController {

    private static MemberController instance;
    private final DataStore store = DataStore.getInstance();

    private MemberController() {}

    public static MemberController getInstance() {
        if (instance == null) instance = new MemberController();
        return instance;
    }

    public ObservableList<Member> getAllMembers() {
        return store.getMembers();
    }

    public void addMember(Member member) {
        store.getMembers().add(member);
    }

    public void deleteMember(Member member) {
        store.getMembers().remove(member);
    }

    public void updateMember(Member member, String name, String email, String phone, String address) {
        member.setName(name);
        member.setEmail(email);
        member.setPhone(phone);
        member.setAddress(address);
    }

    public Member findById(int memberID) {
        return store.getMembers().stream()
                .filter(m -> m.getMemberID() == memberID)
                .findFirst().orElse(null);
    }

    public int nextUserId()   { return store.nextMemberId(); }
    public long countTotal()  { return store.getMembers().size(); }
}