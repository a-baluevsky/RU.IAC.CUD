Attribute VB_Name = "ModuleReviewHelper"

Public Sub MkSlides()
    Dim tr As TextRange
    Set tr = ActiveWindow.selection.TextRange
    'Debug.Print tr.Name   '.TextRange.Text
    Dim st As TextRange
    For Each st In tr.Sentences
        If Len(st.Text) > 1 Then
            Dim sl As Slide
            Set sl = ActivePresentation.Slides.Add(ActivePresentation.Slides.Count, ppLayoutTitleOnly)
            sl.Shapes(1).TextFrame.TextRange.Text = Left$(st.Text, Len(st.Text) - 1)
        End If
    Next st
End Sub

